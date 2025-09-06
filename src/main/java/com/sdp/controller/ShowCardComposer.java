package com.sdp.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Label;

import com.sdp.dao.CreditCardDao;
import com.sdp.model.CreditCard;

public class ShowCardComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	private Label previewNumber;
	private Label previewName;
	private Label previewExpiry;
	private Label previewCVV;
	private Label previewLimit;

	public void setPreviewNumber(Label previewNumber) {
		this.previewNumber = previewNumber;
	}

	public void setPreviewName(Label previewName) {
		this.previewName = previewName;
	}

	public void setPreviewExpiry(Label previewExpiry) {
		this.previewExpiry = previewExpiry;
	}

	public void setPreviewCVV(Label previewCVV) {
		this.previewCVV = previewCVV;
	}

	public void setPreviewLimit(Label previewLimit) {
		this.previewLimit = previewLimit;
	}

	public void loadCard() {
		String accno = (String) Sessions.getCurrent().getAttribute("accountnumber");
		CreditCard card = new CreditCardDao().getCreditCardByAccount(accno);

		if (card != null) {
			previewNumber.setValue(formatCardNumber(card.getCardNumber()));
			previewName.setValue(card.getCardholderName().toUpperCase());
			previewExpiry.setValue(String.format("%02d/%02d", card.getExpMonth(), card.getExpYear() % 100));
			previewCVV.setValue(card.getCvv());
			previewLimit.setValue(String.format("₹ %.2f", card.getLimitAmount()));
		}
	}

	private String formatCardNumber(String number) {
		return number.replaceAll("(.{4})(?!$)", "$1 ");
	}
}
