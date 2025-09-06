package com.sdp.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.sdp.connection.DbConnection;
import com.sdp.model.User;

/**
 * Handles account opening and loan application submission for customers.
 * Provides functionalities for:
 * <ul>
 * <li>Filling and submitting loan application forms</li>
 * <li>Validating loan amount</li>
 * <li>Calculating EMI</li>
 * <li>Sending status emails to customers</li>
 * <li>Retrieving and updating loan application details</li>
 * </ul>
 */
public class AccountOpening extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;
	@Wire
	Textbox name;
	@Wire
	Datebox dob;
	@Wire
	Radiogroup gender;
	@Wire
	Combobox mStatus;
	@Wire
	Textbox mobile;
	@Wire
	Textbox email;

	@Wire
	Combobox occupation;
	@Wire
	Textbox employer;
	@Wire
	Doublebox gross;
	@Wire
	Doublebox net;

	@Wire
	Doublebox loanAmmount;
	@Wire
	Intbox tenure;
	@Wire
	private Textbox loanType;

	@Wire
	Radiogroup repay;

	@Wire
	Combobox propertytype;
	@Wire
	Doublebox propertyvalue;
	@Wire
	Textbox address;

	@Wire
	Checkbox lifeCover;
	@Wire
	Checkbox surakshaLoan;
	@Wire
	Checkbox declaration;

	@Wire
	Doublebox emiResult;
	@Wire
	Textbox totalPayable;
	@Wire
	private Label grossError;
	@Wire
	private Label netError;
	@Wire
	private Label loanError;
	static final String DEAR = "Dear";
	static final String UEMAIL = "email";
	DbConnection db = new DbConnection();

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		String appIdStr = Executions.getCurrent().getParameter("appId");
		if (appIdStr != null && !appIdStr.isEmpty()) {
			long appId = Long.parseLong(appIdStr);
			form(appId);
		}
		User user = (User) Sessions.getCurrent().getAttribute("userDetails");
		String purpose = (String) Sessions.getCurrent().getAttribute("loanType");
		if(user!=null) {
			name.setValue(user.getFullName());
			email.setValue(user.getEmail());
			mobile.setValue(user.getPhoneNumber());
			name.setDisabled(true);
			email.setDisabled(true);
			mobile.setDisabled(true);
		}
		loanType.setValue(purpose);
	}

	@Listen("onBlur = #loanAmmount")
	public void validateLoanAmount() {
		loanError.setVisible(false);

		if (loanAmmount.getValue() == null) {
			loanError.setValue("Loan amount is required");
			loanError.setVisible(true);
		} else if (loanAmmount.getValue() < 50000 || loanAmmount.getValue() > 5000000) {
			loanError.setValue("Loan amount must be between 50,000 and 5,000,000");
			loanError.setVisible(true);
		}
	}

	@Listen("onBlur = #gross")
	public void validateGrossIncome() {
		grossError.setVisible(false);
		if (gross.getValue() == null) {
			grossError.setValue("Gross Income is required");
			grossError.setVisible(true);
		} else if (gross.getValue() < 30000) {
			grossError.setValue("Gross Income must be at least 30,000");
			grossError.setVisible(true);
		}
	}

	@Listen("onBlur = #net")
	public void validateNetIncome() {
		netError.setVisible(false);
		if (net.getValue() == null) {
			netError.setValue("Net Income is required");
			netError.setVisible(true);
		} else if (net.getValue() < 30000) {
			netError.setValue("Net Income must be at least 30,000");
			netError.setVisible(true);
		}
	}

	@Listen("onClick=#bSubmit")
	public void submitApplication() {
		if (!validateDeclaration()) {
			Messagebox.show("Please agree to the terms and conditions.");
			return;
		}
		String accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement("INSERT INTO loan_application "
						+ "(name, dob, gender, marital_status, mobile, email, "
						+ "occupation, employer_name, gross_income, net_income, "
						+ "loan_amount, tenure_months, purpose, repayment_mode, "
						+ "property_type, property_value, property_address, "
						+ "need_life_cover, need_suraksha_loan, agreed_terms,emi,loan_AccountNo,account_number) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, name.getValue());
			Date utilDate = dob.getValue();
			if (utilDate != null) {
				ps.setDate(2, new java.sql.Date(utilDate.getTime()));
			} else {
				ps.setDate(2, null);
			}
			ps.setString(3, getSelectedLabel(gender));
			ps.setString(4, mStatus.getValue());
			ps.setString(5, mobile.getValue());
			ps.setString(6, email.getValue());

			ps.setString(7, occupation.getValue());
			ps.setString(8, employer.getValue());
			ps.setDouble(9, gross.getValue());
			ps.setDouble(10, net.getValue());

			ps.setDouble(11, loanAmmount.getValue());
			ps.setInt(12, tenure.getValue());
			ps.setString(13, loanType.getValue());
			ps.setString(14, getSelectedLabel(repay));

			ps.setString(15, propertytype.getValue());
			ps.setDouble(16, propertyvalue.getValue());
			ps.setString(17, address.getValue());

			ps.setBoolean(18, lifeCover.isChecked());
			ps.setBoolean(19, surakshaLoan.isChecked());
			ps.setBoolean(20, declaration.isChecked());
			ps.setDouble(21, emiResult.getValue());
			ps.setString(22, genarateLoanAccountNum());
			ps.setString(23, accountNumber);
			int rows = ps.executeUpdate();
			if (rows > 0) {

				String accountNo = accountNumber;

				Session zkSession = Sessions.getCurrent();
				zkSession.setAttribute("aId", accountNo);
				zkSession.setAttribute("name", name.getValue());
				zkSession.setAttribute("policyName", loanType.getValue());
				zkSession.setAttribute("emi", emiResult.getValue());
				zkSession.setAttribute(UEMAIL, email.getValue());

				String content = DEAR + " " + name.getValue() + ",\n\n" + "Your application has been received.\n\n"
						+ "Application ID: " + accountNo + "\n" + "Policy Name: " + loanType.getValue() + "\n"
						+ "Current Status: Waiting\n\n" + "Thank you for applying.";

				boolean sent = sendEmail(email.getValue(), content);
				sendEmailToApplicant();
				if (sent) {
					Messagebox.show("Loan application submitted & email sent successfully!");
					Executions.sendRedirect("/homePage.zul");
				} else {
					Messagebox.show("Loan application submitted, but failed to send email.");
				}

				clearForm();
				Session s = Sessions.getCurrent();
				s.setAttribute("hasAppliedLoan", true);

				Executions.sendRedirect("customer/dashboard.zul");
			} else {
				Messagebox.show("Submission failed. Try again.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show("Database error: " + e.getMessage());
		}
	}

	private String getSelectedLabel(Radiogroup group) {
		return (group.getSelectedItem() != null) ? group.getSelectedItem().getLabel() : "ffvb";
	}

	private boolean validateDeclaration() {
		return declaration != null && declaration.isChecked();
	}

	private void clearForm() {
		name.setValue("");
		dob.setValue(null);
		gender.setSelectedIndex(-1);
		mStatus.setValue("");
		mobile.setValue("");
		email.setValue("");

		occupation.setValue("");
		employer.setValue("");
		gross.setValue(null);
		net.setValue(null);

		loanAmmount.setValue(null);
		tenure.setValue(null);
		loanType.setValue("");
		repay.setSelectedIndex(-1);

		propertytype.setValue("");
		propertyvalue.setValue(null);
		address.setValue("");

		lifeCover.setChecked(false);
		surakshaLoan.setChecked(false);
		declaration.setChecked(false);
		emi.setValue(null);
	}

	static final String LPURPOSE = "purpose";

	public void form(long applicationId) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"SELECT name,dob,gender,marital_status,mobile,email,occupation,employer_name,gross_income,net_income,loan_amount,tenure_months,"
								+ " purpose,repayment_mode,property_type,property_value,property_address,need_life_cover,need_suraksha_loan,agreed_terms FROM loan_application WHERE application_id = ?")) {
			ps.setLong(1, applicationId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					name.setValue(rs.getString("name"));
					dob.setValue(rs.getDate("dob"));
					selectRadio(gender, rs.getString("gender"));
					mStatus.setValue(rs.getString("marital_status"));
					mobile.setValue(rs.getString("mobile"));
					email.setValue(rs.getString(UEMAIL));
					occupation.setValue(rs.getString("occupation"));
					employer.setValue(rs.getString("employer_name"));
					gross.setValue(rs.getDouble("gross_income"));
					net.setValue(rs.getDouble("net_income"));
					loanAmmount.setValue(rs.getDouble("loan_amount"));
					tenure.setValue(rs.getInt("tenure_months"));
					loanType.setValue(rs.getString(LPURPOSE));
					selectRadio(repay, rs.getString("repayment_mode"));
					propertytype.setValue(rs.getString("property_type"));
					propertyvalue.setValue(rs.getDouble("property_value"));
					address.setValue(rs.getString("property_address"));
					lifeCover.setChecked(rs.getBoolean("need_life_cover"));
					surakshaLoan.setChecked(rs.getBoolean("need_suraksha_loan"));
					declaration.setChecked(rs.getBoolean("agreed_terms"));
				} else {
					Messagebox.show("No application found with ID: " + applicationId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Messagebox.show("Database error: " + e.getMessage());
		}
	}

	private void selectRadio(Radiogroup group, String label) {
		group.getItems().stream().filter(item -> item.getLabel().equals(label)).findFirst()
				.ifPresent(group::setSelectedItem);
	}

	public void sendStatusEmail(String accNo, String status) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"SELECT name, email, purpose,emi FROM loan_application WHERE account_number = ?");) {

			ps.setString(1, accNo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String cName = rs.getString("name");
				String cEmail = rs.getString(UEMAIL);
				String policyName = rs.getString(LPURPOSE);
				double lEmi = rs.getDouble("emi");
				String content;
				if ("Rejected".equalsIgnoreCase(status)) {
					content = DEAR + " " + cName + ",\n\n" + "We regret to inform you that your loan application for '"
							+ policyName + "' " + "(Account No: " + accNo
							+ ") has been rejected after careful review.\n\n"
							+ "If you have any questions or believe this was a mistake, please contact our support team.\n\n"
							+ "Thank you for considering us.\n\n" + "Regards,\nLoan Department";
				} else if ("Approved".equalsIgnoreCase(status)) {
					content = DEAR + " " + cName + ",\n\n" + "Congratulations! Your loan application for '" + policyName
							+ "' " + "(AccountNo: " + accNo + ") has been approved.\n\n" + "Your Monthly EMI: ₹" + lEmi
							+ "\n\n" + "Our team will contact you shortly for disbursement details.\n\n"
							+ "Thank you for banking with us.\n\n" + "Regards,\nLoan Department";
				} else {
					content = DEAR + " " + cName + ",\n\n" + "Your application for " + policyName + " (Application ID: "
							+ accNo + ") has been updated.\n\n" + "Current Status: " + status + "\n\n" + "Thank you.";
				}

				boolean sent = sendEmail(cEmail, content);

				if (sent) {
					Messagebox.show("Email sent to applicant.");
				} else {
					Messagebox.show("Failed to send email.");
				}
			} else {
				Messagebox.show("Cannot send email: application not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEmailToApplicant() {
		Session zkSession = Sessions.getCurrent();
		String cName = (String) zkSession.getAttribute("name");
		String accountNo = (String) zkSession.getAttribute("account_number");
		String policyName = (String) zkSession.getAttribute("policyName");
		String applicantEmail = (String) zkSession.getAttribute(UEMAIL);
		String status = (String) zkSession.getAttribute("status");

		if (status == null) {
			status = "Waiting";
		}

		String content = "Dear " + cName + ",\n\n" + "Your application has been received.\n\n" + "Application ID: "
				+ accountNo + "\n" + "Policy Name: " + policyName + "\n" + "Current Status: " + status + "\n\n"
				+ "Thank you for applying.";

		boolean sent = sendEmail(applicantEmail, content);

		if (sent) {
			Messagebox.show("Email sent successfully to applicant!");
			Executions.sendRedirect("/HomePage/homePage.zul");
		} else {
			Messagebox.show("Failed to send email.");
		}
	}

	public boolean sendEmail(String toEmail, String content) {

		String host = "smtp.gmail.com";
		String from = "itspriya0304@gmail.com";
		String password = "tqfb ablx wxal qszb";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		javax.mail.Session session = javax.mail.Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject("Application Confirmation");
			message.setText(content);

			Transport.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Wire
	Label emi;
	@Wire
	Label totalpay;

	@Listen("onChange=#loanAmmount")
	public void onLoanAmountChange() {
		calculateEMI();
	}

	@Listen("onChange=#tenure")
	public void onTenureChange() {
		calculateEMI();
	}

	public void calculateEMI() {
		double principal = loanAmmount.getValue();
		double annualRate = 9.91;
		double r = annualRate / 12 / 100;
		int n = tenure.getValue();

		double e;
		if (r > 0) {
			e = (principal * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
		} else {
			e = principal / n;
		}

		double emiRounded = Math.ceil(e);
		double total = emiRounded * n;

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
		emiResult.setValue(emiRounded);
		totalPayable.setValue("₹" + df.format(total));

		emiResult.setVisible(true);
		totalPayable.setVisible(true);
		emi.setVisible(true);
		totalpay.setVisible(true);
	}

	/*
	 * Update User Profile
	 */
	@Listen("onClick=#btnEdit")
	public void updateUserProfile() {
		String sqlUpdate = "UPDATE loan_application SET "
				+ "name=?, dob=?, gender=?, marital_status=?, mobile=?, email=?, "
				+ "occupation=?, employer_name=?, gross_income=?, net_income=?, "
				+ "loan_amount=?, tenure_months=?, purpose=?, repayment_mode=?, "
				+ "property_type=?, property_value=?, property_address=? " + "WHERE application_id=?";
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlUpdate);) {

			Customer app = new Customer();
			ps.setString(1, app.getName());

			java.util.Date dateOfBirth = app.getDob();
			if (dob != null) {
				ps.setDate(2, new java.sql.Date(dateOfBirth.getTime()));
			} else {
				ps.setNull(2, java.sql.Types.DATE);
			}

			ps.setString(3, app.getGender());
			ps.setString(4, app.getMaritalStatus());
			ps.setString(5, app.getMobile());
			ps.setString(6, app.getEmail());
			ps.setString(7, app.getOccupation());
			ps.setString(8, app.getEmployerName());
			ps.setBigDecimal(9, app.getGrossIncome());
			ps.setBigDecimal(10, app.getNetIncome());
			ps.setBigDecimal(11, app.getLoanAmount());
			ps.setNull(12, java.sql.Types.INTEGER);
			ps.setString(13, app.getPurpose());
			ps.setString(14, app.getRepaymentMode());
			ps.setString(15, app.getPropertyType());
			ps.setBigDecimal(16, app.getPropertyValue());
			ps.setString(17, app.getPropertyAddress());

			ps.setLong(18, app.getApplicationId());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	String sql = "SELECT application_id, name, dob, gender, marital_status, mobile, email, occupation, employer_name, "
			+ "gross_income, net_income, loan_amount, tenure_months, emi, purpose, repayment_mode, "
			+ "property_type, property_value, property_address, need_life_cover, need_suraksha_loan, "
			+ "loan_AccountNo,status " + "FROM loan_application WHERE account_number=?";

	public Customer getLoanApplicationByAc(String accNo) {
		Customer app = null;
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
			ps.setString(1, accNo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				app = new Customer();
				app.setApplicationId(rs.getInt("application_id"));
				app.setName(rs.getString("name"));
				app.setDob(rs.getDate("dob"));
				app.setGender(rs.getString("gender"));
				app.setMaritalStatus(rs.getString("marital_status"));
				app.setMobile(rs.getString("mobile"));
				app.setEmail(rs.getString(UEMAIL));
				app.setOccupation(rs.getString("occupation"));
				app.setEmployerName(rs.getString("employer_name"));
				app.setGrossIncome(rs.getBigDecimal("gross_income"));
				app.setNetIncome(rs.getBigDecimal("net_income"));
				app.setLoanAmount(rs.getBigDecimal("loan_amount"));
				app.setTenureMonths(rs.getInt("tenure_months"));
				app.setEmi(rs.getDouble("emi"));
				app.setPurpose(rs.getString(LPURPOSE));
				app.setRepaymentMode(rs.getString("repayment_mode"));
				app.setPropertyType(rs.getString("property_type"));
				app.setPropertyValue(rs.getBigDecimal("property_value"));
				app.setPropertyAddress(rs.getString("property_address"));
				app.setNeedLifeCover(rs.getBoolean("need_life_cover"));
				app.setNeedSurakshaLoan(rs.getBoolean("need_suraksha_loan"));
				app.setAccountNo(rs.getString("loan_AccountNo"));
				app.setStatus(rs.getString("status"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return app;
	}

	public String genarateLoanAccountNum() {
		try (Connection con = db.getConnection();
				PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(1) as ac FROM loan_application");
				PreparedStatement ps = con
						.prepareStatement("SELECT MAX(loan_AccountNo) as acc FROM loan_application");) {

			ResultSet rs1 = ps1.executeQuery();
			rs1.next();
			if (rs1.getInt("ac") < 1) {
				return "L0001";
			}

			ResultSet rs = ps.executeQuery();
			rs.next();
			String loanAcc = rs.getString("acc");
			long num = Long.parseLong(loanAcc.substring(1));
			num++;
			return String.format("L%04d", num);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Wire
	private Label employerLbl;
	@Wire
	private Label grossLbl;
	@Wire
	private Label netLbl;

	@Listen("onChange = #occupation")
	public void changeFields() {
		String selected = occupation.getValue();

		if ("Salaried".equalsIgnoreCase(selected)) {
			employerLbl.setValue("Employer:");
			employer.setVisible(true);
			grossLbl.setValue("Gross Salary:");
			netLbl.setValue("Net Salary:");

		} else if ("Self-Employed".equalsIgnoreCase(selected)) {
			employerLbl.setValue("Firm/Shop Name:");
			employer.setVisible(true);
			grossLbl.setValue("Gross Revenue:");
			netLbl.setValue("Net Profit:");

		} else if ("Business".equalsIgnoreCase(selected)) {
			employerLbl.setValue("Business Name:");
			employer.setVisible(true);
			grossLbl.setValue("Turnover:");
			netLbl.setValue("Net Profit:");

		} else if ("Retired".equalsIgnoreCase(selected)) {
			employerLbl.setValue("Previous Employer:");
			employer.setVisible(true);
			grossLbl.setValue("Pension Amount:");
			netLbl.setValue("Other Income:");
		}
	}
}
