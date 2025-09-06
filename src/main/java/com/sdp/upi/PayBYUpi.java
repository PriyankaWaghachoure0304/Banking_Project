package com.sdp.upi;

import java.text.SimpleDateFormat;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import com.sdp.dao.PayBYScannerDao;
import com.sdp.dao.UpiDao;
import com.sdp.model.Receipt;

public class PayBYUpi extends SelectorComposer<Div> {
    private static final long serialVersionUID = 1L;
	@Wire
    Doublebox amount;
    @Wire
    Textbox remark;
    @Wire
    Div popup1;
    @Wire Div  popup2;
    @Wire Div  mpinPopup;   
    @Wire
    Label toName;
    @Wire Label  txnIdLbl;
    @Wire Label  timeLbl;
    @Wire Label  toAccLbl;
    @Wire Label  amtLbl;
    @Wire Label  statusMsg;
    @Wire
    Intbox mpinBox;

    transient UpiDao udao = new UpiDao();
  transient  PayBYScannerDao dao = new PayBYScannerDao();

    String upiId;
    String fromAccount;
    String toAccount;
    
    @Wire
    Button topay;

    @Override
    public void doAfterCompose(Div comp) throws Exception {
        super.doAfterCompose(comp);
        upiId = (String) Sessions.getCurrent().getAttribute("upiId");
        fromAccount = (String) Sessions.getCurrent().getAttribute("accountnumber");
        toAccount = udao.getAccountNum(upiId);
        if(toAccount==null) {
        	Messagebox.show("Upi Id Not Valid!");
        	amount.setDisabled(true);
        	remark.setDisabled(true);
        	topay.setDisabled(true);
        	return;
        }
        if(toAccount.equals(fromAccount)) {
        	Messagebox.show("You are trying to send money from and to the same bank account. Please check account details and try again.");
        	amount.setDisabled(true);
        	remark.setDisabled(true);
        	topay.setDisabled(true);
        	return;
        }
        toName.setValue(dao.getName(toAccount));
    }

    @Listen("onClick=#topay")
    public void pay() {
        if (amount.getValue() == null || amount.getValue() <= 0) {
            Messagebox.show("Please enter a valid amount.");
            return;
        }
        if (remark.getValue() == null || remark.getValue().trim().isEmpty()) {
            Messagebox.show("Please enter a remark.");
            return;
        }
        mpinPopup.setVisible(true);   
    }

    @Listen("onClick=#confirmBtn")
    public void verifyMpin() {
        Integer enteredMpin = mpinBox.getValue();
        if (enteredMpin == null || enteredMpin.toString().length() != 4) {
            Messagebox.show("Please enter a valid 4-digit MPIN.");
            return;
        }

        int correctMpin = dao.getMpin(fromAccount);

        mpinPopup.setVisible(false); 

        if (enteredMpin == correctMpin) {
            boolean success = dao.transferByScanner(fromAccount, toAccount, amount.getValue(), remark.getValue());
            if (success) {
                setPopup();
                popup1.setVisible(true); 
            } else {
                statusMsg.setValue("Transaction Failed due to server/balance issue.");
                popup2.setVisible(true);  
            }
        } else {
            statusMsg.setValue("Incorrect MPIN entered.");
            popup2.setVisible(true);     
        }
        
        amount.setValue(null);
        remark.setValue("");
        mpinBox.setValue(null);
    }

    public void setPopup() {
        Receipt receipt = dao.getReceipt();
        txnIdLbl.setValue(receipt.getTransactionId());
        timeLbl.setValue(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date()));
        toAccLbl.setValue(upiId);
        amtLbl.setValue(receipt.getAmount() + "");
    }
}
