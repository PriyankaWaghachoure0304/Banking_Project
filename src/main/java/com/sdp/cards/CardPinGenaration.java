package com.sdp.cards;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.core.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.connection.DbConnection;
import com.sdp.model.DebitCardPojo;

public class CardPinGenaration extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;


	@Wire
	Textbox cardnumber;

	@Wire
	Button pinsubmit;
	@Wire
	Button pincancel;
	
	String cardnumber1;
	Logger logger = LoggerExample.getLogger();
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		DebitCardPojo card = (DebitCardPojo) Sessions.getCurrent().getAttribute("cardDetails");
		if(card!=null) {
		cardnumber1 = card.getCardNumber();
		}
		else {
			Executions.sendRedirect("/homePage.zul");
		}
		cardnumber.setValue(cardnumber1);
		cardnumber.setDisabled(true);
	}

	DbConnection db = new DbConnection();

	@Wire
	Textbox newpin;
	@Wire
	Textbox conpin;


	private static final String FETCHCARDPIN = "select pin from debit_card where card_number=?";

	public String getPin() {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCHCARDPIN)) {

			ps.setString(1, cardnumber1);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("pin");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String INSERTCARDPIN = "update debit_card set pin=? where card_number=?";

	@Listen("onClick=#pinsubmit")
	public void pingenaration() {

	    String pin1Str = newpin.getValue();
	    String pin2Str = conpin.getValue();

	    if (getPin() != null) {
	        Messagebox.show("PIN Already Exists....");
			logger.warn("Enter valide credentials");
	        pinsubmit.setDisabled(true);
	        return;
	    }

	    if (pin1Str == null || pin2Str == null || pin1Str.isEmpty() || pin2Str.isEmpty()) {
	        Messagebox.show("Enter All Details!!!");
	        return;
	    }

	    if (!pin1Str.matches("\\d{4}") || !pin2Str.matches("\\d{4}")) {
	        Messagebox.show("PIN must be exactly 4 digits");
	        return;
	    }

	    if (!pin1Str.equals(pin2Str)) {
	        Messagebox.show("PIN does not match...");
	        logger.warn("Enter valide pin");
	        return;
	    }

	    try (Connection con = db.getConnection(); 
	         PreparedStatement ps = con.prepareStatement(INSERTCARDPIN)) {

	        int pinValue = Integer.parseInt(pin2Str); 
	        ps.setInt(1, pinValue);
	        ps.setString(2, cardnumber1);

	        int row = ps.executeUpdate();
	        if (row > 0) {
	            Messagebox.show("PIN Generated Successfully...");
	            newpin.setValue("");
	            conpin.setValue("");
	        } else {
	            Messagebox.show("PIN Not Set...");
	            logger.warn("Pin not set");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@Listen("onClick=#pincancel")
	public void pingenarationCancel() {
		newpin.setValue(null);
		conpin.setValue(null);
	}
}

