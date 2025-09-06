package com.sdp.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class TransactionStatusComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;
	@Wire private Window statusWin;
	@Wire private Label statusTitle;
	@Wire private Label  statusMessage;
	@Wire private Label  txnIdLbl;
	@Wire private Label  timeLbl;
	@Wire private Label  toAccLbl;
	@Wire private Label  amtLbl;
	@Wire private Image statusIcon;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		String txnId = (String) Executions.getCurrent().getArg().get("txnId");
		String toAccount = (String) Executions.getCurrent().getArg().get("toAccount");
		double amount = (double) Executions.getCurrent().getArg().get("amount");
		LocalDateTime txnTime = (LocalDateTime) Executions.getCurrent().getArg().get("txnTime");
		boolean success = (boolean) Executions.getCurrent().getArg().get("success");

		if (success) {
			statusTitle.setValue("Payment Successful");
			statusMessage.setValue("₹" + amount + " was transferred to " + toAccount + " successfully.");
			statusIcon.setSrc("/img/success.png");
		} else {
			statusTitle.setValue("Payment Failed");
			statusMessage.setValue("Your transaction could not be completed. Please try again.");
			statusIcon.setSrc("/img/failure.png");
		}

		txnIdLbl.setValue(txnId);
		toAccLbl.setValue(toAccount);
		amtLbl.setValue("₹" + amount);
		timeLbl.setValue(txnTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
	}
}
