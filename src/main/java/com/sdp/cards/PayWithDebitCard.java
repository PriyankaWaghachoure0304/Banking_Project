package com.sdp.cards;

import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;

import com.sdp.dao.DebitCardDao;
import com.sdp.model.DebitCardPojo;
import com.sdp.transaction.TransferDao;

public class PayWithDebitCard extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	Textbox senderCardNumber;
	@Wire
	Textbox reciverCardNumber;
	@Wire
	Textbox expiry;
	@Wire
	Textbox holderName;
	@Wire
	Textbox remark;
	@Wire
	Textbox cardPin;
	@Wire
	Intbox cvvnum;
	@Wire
	Label amountlabel;
	@Wire
	Label remarkLabel;
	@Wire
	Doublebox amountBox;
	@Wire
	Popup pinPopup;
	@Wire
	Button payButton;

	private Div overlay;

	String fromaccountnum;
	 transient DebitCardPojo  card;

	@Listen("onOK=#cardPin")
	public void ok() {
		submitPay();
	}

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		if (Sessions.getCurrent().getAttribute("accountnumber") != null
				|| Sessions.getCurrent().getAttribute("cardDetails") != null) {
			fromaccountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");

			card = (DebitCardPojo) Sessions.getCurrent().getAttribute("cardDetails");
			String sendercardNumber = card.getCardNumber();

			senderCardNumber.setValue(sendercardNumber);
			senderCardNumber.setDisabled(true);
		} else {
			Messagebox.show("Login Please...");
		}
	}

	@Listen("onClick=#verify")
	public void verifyReciverCard() {
		String reciverCard = reciverCardNumber.getValue().trim();
		String expiryDate = expiry.getValue().trim();
		String cardHolderName = holderName.getValue().trim();
		Integer cvv = cvvnum.getValue();

		if (reciverCard.length() != 16) {
			alert("Card number must be exactly 16 digits!");
			return;
		}

		DebitCardDao ddao = new DebitCardDao();
		DebitCardPojo cardDetails = ddao.fetchDebitcardwithDebitcard(reciverCard);

		if (cardDetails == null) {
			alert("No such card exists. Please check the number again.");
			return;
		}

		if (cardDetails.getCardNumber().equals(reciverCard)
				&& cardDetails.getCardHolderName().equalsIgnoreCase(cardHolderName)
				&& cardDetails.getValidExp().equals(expiryDate) && cardDetails.getCvv() == cvv
				&& !senderCardNumber.getValue().equals(reciverCard)) {
			alert("Card Verified ✅ Proceeding with Payment...");
			amountlabel.setVisible(true);
			amountBox.setVisible(true);
			payButton.setVisible(true);
			remarkLabel.setVisible(true);
			remark.setVisible(true);
		} else {
			alert("Invalid card details. Please try again.");
			amountlabel.setVisible(false);
			amountBox.setVisible(false);
			payButton.setVisible(false);
			remarkLabel.setVisible(false);
			remark.setVisible(false);
		}
	}

	transient TransferDao tDao = TransferDao.getTransferDao();

	@Listen("onClick=#payButton")
	public void payAmount() {
		if (overlay == null) {
			overlay = new Div();
			overlay.setSclass("modal-overlay");
			pinPopup.getParent().appendChild(overlay);
		}
		overlay.setVisible(true);

		pinPopup.open(overlay, "center");
		cardPin.setFocus(true);
	}

	@Listen("onClick=#submit")
	public void submitPay() {
		overlay.removeSclass();
		overlay.setVisible(false);
		overlay = null;
		pinPopup.close();
		String reciverCard = reciverCardNumber.getValue().trim();
		DebitCardDao ddao = new DebitCardDao();
		DebitCardPojo cardDetails = ddao.fetchDebitcardwithDebitcard(reciverCard);
		String toAccountNum = cardDetails.getAccountNumber();
		if (card.getPin() == Integer.parseInt(cardPin.getValue())) {

			tDao.transferAmount(fromaccountnum, toAccountNum, amountBox.getValue(), "ATM", 0.0, remark.getValue());
			clearAll();
		} else {
			Messagebox.show("Incorrect Pin");
		}

	}

	public void clearAll() {

		reciverCardNumber.setValue("");
		expiry.setValue("");
		holderName.setValue("");
		remark.setValue("");
		cardPin.setValue("");

		cvvnum.setValue(null);

		amountBox.setValue(null);
	}

}
