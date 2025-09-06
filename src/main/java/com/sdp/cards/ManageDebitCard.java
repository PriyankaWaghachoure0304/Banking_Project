package com.sdp.cards;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import com.sdp.connection.DbConnection;
import com.sdp.dao.DebitCardDao;
import com.sdp.model.DebitCardPojo;

public class ManageDebitCard extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Label accno;
	@Wire
	Label cardNumber;
	@Wire
	Label valid;
	@Wire
	Label cvv;

	@Wire
	Checkbox showCard;
	
	@Wire
	Checkbox showCVV;

	DbConnection db = new DbConnection();

	
	String accountnum;
	
	
	static final String CARDDETAILS="cardDetails";
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
		accno.setValue("Your Account Number Is : " + accountnum);
		cardNumber.setValue("XXXX  XXXX  XXXX  XXXX");
		valid.setValue("VALID THRU: **/**");
		cvv.setValue("CVV:  XXX  ");
		if(accountnum!=null){
			DebitCardDao ddao = new DebitCardDao();
			DebitCardPojo card = ddao.fetchDebitcard(accountnum);
			 Sessions.getCurrent().setAttribute(CARDDETAILS, card);
		}
	}

	@Listen("onCheck=#showCard")
	public void shownumber() {
		if (showCard.isChecked()) {
			if (accountnum != null) {
				DebitCardPojo card = (DebitCardPojo) Sessions.getCurrent().getAttribute(CARDDETAILS);
				String cardno = card.getCardNumber();
				String space = "";
				for (int i = 0; i < cardno.length(); i++) {
					space += cardno.charAt(i);
					if (i == 3 || i == 7 || i == 11) {
						space += "  ";
					}
				}
				
				cardNumber.setValue(space);
				valid.setValue("VALID THRU: "+card.getValidExp());
			}
		}
		else {
			cardNumber.setValue("XXXX  XXXX  XXXX  XXXX");
			valid.setValue("VALID THRU: **/**");
		}
	}

	@Listen("onCheck=#showCVV")
	public void showcvv() {
		if (showCVV.isChecked()) {
			DebitCardPojo card = (DebitCardPojo) Sessions.getCurrent().getAttribute(CARDDETAILS);
			if(card!=null) {
				int cvvno = card.getCvv();
				cvv.setValue("CVV:  "+cvvno);
			}
		}
		else {
			cvv.setValue("CVV: XXX ");
			
		}
	}

	
	
	

}
