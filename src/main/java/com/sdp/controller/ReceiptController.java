package com.sdp.controller;

import java.text.SimpleDateFormat;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.sdp.model.Receipt;
import com.sdp.transaction.TransferDao;

public class ReceiptController extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Label txnId;
	@Wire
	Label fromAcc;
	@Wire
	Label toAcc;
	@Wire
	Label amount;
	@Wire
	Label txnType;
	@Wire
	Label tax;
	@Wire
	Label remark;
	@Wire
	Label availableBalance;
	@Wire
	Label time;

	@Wire
	Window r_window;

	String accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");

	transient TransferDao tdao = TransferDao.getTransferDao();

	@Wire
	Vlayout vlayout;

	@Listen("onClick=#r1")
	public void getReceipt() {
		vlayout.setVisible(true);
		Receipt receipt = tdao.getReceipt(accountNumber);

		txnId.setValue(receipt.getTransactionId() + "");
		fromAcc.setValue(receipt.getFromAccount());
		toAcc.setValue(receipt.getToAccount());
		amount.setValue("₹" + receipt.getAmount());
		txnType.setValue(receipt.getTransactionType());
		tax.setValue("₹" + receipt.getTax());
		remark.setValue(receipt.getRemark());
		availableBalance.setValue("₹" + receipt.getAvailableBalance());
		time.setValue(receipt.getTime() + "");
		time.setValue(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date()));

	}

}
