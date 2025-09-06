package com.sdp.cards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.sdp.connection.DbConnection;
import com.sdp.model.User;

public class CreditFormComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	@Wire
	private Textbox accNo;
	@Wire
	private Textbox fullName;
	@Wire
	private Textbox mobile;
	@Wire
	private Textbox email;
	@Wire
	private Textbox address;
	@Wire
	private Textbox idProof;
	@Wire
	private Datebox dob;
	@Wire
	private Doublebox limit;
	String accountNo;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		User user = (User) Sessions.getCurrent().getAttribute("userDetails");
		if(user!=null) {
			fullName.setValue(user.getFullName());
			email.setValue(user.getEmail());
			mobile.setValue(user.getPhoneNumber());
			fullName.setDisabled(true);
			email.setDisabled(true);
			mobile.setDisabled(true);
		}

		dob.setConstraint(new Constraint() {
			@Override
			public void validate(Component comp, Object value) throws WrongValueException {
				if (value == null) {
					throw new WrongValueException(dob, "Date cannot be empty");
				}

				java.util.Calendar selected = Calendar.getInstance();
				selected.setTime((Date) value);
				selected.set(Calendar.HOUR_OF_DAY, 0);
				selected.set(Calendar.MINUTE, 0);
				selected.set(Calendar.SECOND, 0);
				selected.set(Calendar.MILLISECOND, 0);

				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);

				if (selected.after(today)) {
					throw new WrongValueException(dob, "Date cannot be in the future!");
				}
			}
		});

		Boolean login = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		if (login != null && login) {
			accountNo = (String) Sessions.getCurrent().getAttribute("accountnumber");
			accNo.setValue(accountNo);
		} else {
			Executions.sendRedirect("/LoginPage.zul");
		}
	}
	
	@Listen("onBlur = #limit")
	public void validateLoanAmount() {

		if (limit.getValue() == null) {
			Messagebox.show(limit.getValue()+ "Enter valid Limit");
		} else if (limit.getValue() < 10000 || limit.getValue() > 500000) {
			Messagebox.show(" Amount must be between 10,000 and 5,000,00");
		}
	}

	@Listen("onClick=#submitBtn")
	public void submitForm() {
		String acc = accNo.getValue().trim();

		if (acc.isEmpty()) {
			Messagebox.show("Account Number is required!");
			return;
		}
		DbConnection db = new DbConnection();
		try (Connection con = db.getConnection()) {

			String checkSql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
			try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
				checkPs.setString(1, acc);
				ResultSet rs = checkPs.executeQuery();
				rs.next();
				if (rs.getInt(1) == 0) {
					Messagebox.show("Invalid Account Number. Please enter a valid one.");
					return;
				}
			}

			String insertSql = "INSERT INTO credit_card_requests "
					+ "(account_number, full_name, dob, mobile, email, address, id_proof, limit_amount) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement ps = con.prepareStatement(insertSql)) {
				ps.setString(1, acc);
				ps.setString(2, fullName.getValue());
				ps.setDate(3, dob.getValue() != null ? new java.sql.Date(dob.getValue().getTime()) : null);
				ps.setString(4, mobile.getValue());
				ps.setString(5, email.getValue());
				ps.setString(6, address.getValue());
				ps.setString(7, idProof.getValue());
				ps.setDouble(8, limit.getValue() != null ? limit.getValue() : 0.0);

				ps.executeUpdate();
				Messagebox.show("✅ Credit Card Request Submitted Successfully!");
				resetForm();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Listen("onClick=#resetBtn")
	public void resetForm() {
		accNo.setValue("");
		fullName.setValue("");
		dob.setValue(null);
		mobile.setValue("");
		email.setValue("");
		address.setValue("");
		idProof.setValue("");
		limit.setValue(null);
	}

	@Listen("onBlur = #limit")
	public void validateCreditCardLimit() {
		Double value = limit.getValue();

		if (value == null) {
			throw new WrongValueException(limit, "Requested limit cannot be empty!");
		} else if (value <= 0) {
			throw new WrongValueException(limit, "Requested limit must be a positive number!");
		} else if (value < 10000 || value > 500000) {
			throw new WrongValueException(limit, "Requested limit must be between 10,000 and 500,000!");
		} else if (value % 1000 != 0) {
			throw new WrongValueException(limit, "Requested limit must be in multiples of 1,000!");
		}
	}

}