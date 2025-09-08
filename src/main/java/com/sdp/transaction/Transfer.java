package com.sdp.transaction;

import java.text.DecimalFormat;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;

import com.sdp.dao.MPinDao;
import com.sdp.model.TransferPojo;

public class Transfer extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Doublebox amountBox;

	@Wire
	Radiogroup transferType;

	@Wire
	Label statusLabel;

	@Wire
	Label charge;
	@Wire Label  fromName;
	@Wire Label  toName;
	@Wire Label  amtTax;

	
	@Wire
	Combobox fromAccount;
	
	@Wire
	Combobox toAccount;
	
	@Wire
	Textbox remarks;
	
	@Wire
	Doublebox newAmountBox;
	
transient	TransferDao tDao = TransferDao.getTransferDao();
	
	@Wire
	Button setMpin;
	@Wire Button  confirmPass;
	
	@Wire
	Div check_mpin;
	
	
	static final String TRANSFER="transferDetail";
	
	String accountNumber = (String) Sessions.getCurrent().getAttribute("accountnumber");
	

	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		
		Boolean isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
		if(isLoggedIn!=null && isLoggedIn) {
		
		if(tDao.isExistMpin(accountNumber)==-1) {
			
			Messagebox.show("Set MPin first");
			
			fromAccount.setValue(accountNumber);
			String name = tDao.getName(fromAccount.getValue());
			fromName.setValue(name);
			fromAccount.setDisabled(true);
		}
		else {
			fromAccount.setValue(accountNumber);
			String name = tDao.getName(fromAccount.getValue());
			fromName.setValue(name);
			fromAccount.setDisabled(true);
			
		}
		}
		else {
			Messagebox.show("Login First!");
		}
		
		
	}	
	
	@Listen("onBlur=#toAccount")	
	public void matchToAccout() {
		String from = fromAccount.getValue();
		String to = toAccount.getValue();
		
	
		if(from!=null && from.equals(to)) {
			statusLabel.setValue("Please Enter Another Account Number!");
			toAccount.setValue("");
			toName.setValue("");
			toAccount.setFocus(true);
		}
		else {
			statusLabel.setValue("");
		}
	}
		
transient TransferPojo tPojo;

	@Listen("onClick=#submitBtn")
	public void pay() {
	    if (!areMandatoryFieldsFilled()) {
	        statusLabel.setValue("Please Enter All Mandatory Fields (*)");
	        return;
	    }

	    if (!isMpinSet()) {
	        Messagebox.show("Set MPin first");
	        submitBtn.setDisabled(true);
	        return;
	    }

	    double amount = amountBox.getValue();
	    String transferTypeName = transferType.getSelectedItem().getLabel();

	    if (!isValidAmount(amount)) {
	        statusLabel.setValue("Amount must be greater than 0");
	        return;
	    }

	    if (!isWithinTransferLimits(transferTypeName, amount)) {
	        return; 
	    }

	    createTransferPojo(amount, transferTypeName);

	    Sessions.getCurrent().setAttribute(TRANSFER, tPojo);
	    check_mpin.setVisible(true);
	    emptyField();
	}

	private boolean areMandatoryFieldsFilled() {
	    return !(toAccount.getValue().equals("")
	            || amountBox.getValue().equals("")
	            || transferType.getSelectedItem() == null);
	}

	private boolean isMpinSet() {
	    return tDao.isExistMpin(accountNumber) != 0;
	}

	private boolean isValidAmount(double amount) {
	    return amount >= 1;
	}

	private boolean isWithinTransferLimits(String transferTypeName, double amount) {
	    switch (transferTypeName) {
	        case "NEFT":
	            if (amount > 500000) {
	                statusLabel.setValue("Maximum limit exceeded!  NEFT(Max LIMIT: 500000)");
	                return false;
	            }
	            break;

	        case "IMPS":
	            if (amount > 1000000) {
	                statusLabel.setValue("Maximum limit exceeded!  IMPS(Max LIMIT: 1000000)");
	                return false;
	            }
	            break;

	        case "RTGS":
	            if (amount < 200000) {
	                statusLabel.setValue("RTGS(MIN LIMIT: 200000)");
	                return false;
	            } else if (amount > 100000000) {
	                statusLabel.setValue("Maximum limit exceeded!  RTGS(Max LIMIT: 100000000)");
	                return false;
	            }
	            break;

	        default:
	            break;
	    }
	    return true;
	}

	private void createTransferPojo(double amount, String transferTypeName) {
	    tPojo = new TransferPojo();
	    tPojo.setFromAccount(fromAccount.getValue());
	    tPojo.setToAccount(toAccount.getValue());
	    tPojo.setAmount(amount);
	    tPojo.setTransferType(transferTypeName);
	    tPojo.setCharges(charge());
	    tPojo.setRemark(remarks.getValue());
	}

	

	
	@Wire
	Button submitBtn;
	
	@Listen("onCheck=#transferType")
	public double charge() {
	    if (amountBox.getValue() != null) {
	        double amount = amountBox.getValue();
	        DecimalFormat df = new DecimalFormat("#,##0.00");
	        submitBtn.setLabel("Pay  ₹" + df.format(amount));

	        String transferTypeName = transferType.getSelectedItem().getLabel();
	        double charges = calculateCharges(transferTypeName, amount);

	        double newAmt = amount + charges;
	        newAmountBox.setValue(newAmt);

	        charge.setValue("charges: " + charges);
	        return charges;
	    } else {
	        statusLabel.setValue("Please enter amount!");
	    }
	    return 0;
	}

	private double calculateCharges(String transferTypeName, double amount) {
	    switch (transferTypeName) {
	        case "NEFT":
	            return calculateNEFTCharges(amount);
	        case "IMPS":
	            return calculateIMPSCharges(amount);
	        case "RTGS":
	            return calculateRTGSCharges(amount);
	        default:
	            return 0;
	    }
	}

	private double calculateNEFTCharges(double amount) {
	    if (amount <= 10000)
	        return 2.5;
	    else if (amount <= 100000)
	        return 5;
	    else if (amount <= 200000)
	        return 15;
	    else
	        return 25;
	}

	private double calculateIMPSCharges(double amount) {
	    if (amount < 2000)
	        return 0;
	    else if (amount <= 10000)
	        return 2.5;
	    else if (amount <= 100000)
	        return 5;
	    else if (amount <= 200000)
	        return 15;
	    else
	        return 25;
	}

	private double calculateRTGSCharges(double amount) {
	    if (amount >= 200000 && amount <= 500000)
	        return 25;
	    else if (amount > 500000)
	        return 50;
	    return 0;
	}

	
	@Listen("onChange=#fromAccount")
	public void showFromName() {
		String name = tDao.getName(fromAccount.getValue());
		fromName.setValue(name);
		
		
	}
	
	@Listen("onChange=#toAccount")
	public void showToName() {
		
		String name = tDao.getName(toAccount.getValue());
		if(name.equals("Account Number not found!")){
			toName.setStyle("color:red;");
			toName.setValue(name);
			
		}
		else {
			toName.setValue(name);
			toName.setStyle("color:green;");
		}
		
		
	}
	
	
	@Wire
	Button r1;
	
	@Wire
	Textbox password;
	
	@Wire
	Div r_window;
	@Wire Div  main_check_container;
	
transient MPinDao dao = new MPinDao();
	
		@Listen("onClick=#confirmPass")
		public void onCheckPass() {
			if(Sessions.getCurrent().getAttribute(TRANSFER)!=null) {
			
			int pass = Integer.parseInt(password.getValue());
			String accountNO = (String) Sessions.getCurrent().getAttribute("accountnumber");
			
			if(dao.checkMPin(pass, accountNO)) {
				TransferPojo tPojo = (TransferPojo) Sessions.getCurrent().getAttribute(TRANSFER);
				
				if(tDao.transferAmount(tPojo.getFromAccount(), tPojo.getToAccount(), tPojo.getAmount(), tPojo.getTransferType(), tPojo.getCharges(), tPojo.getRemark())) {
					
					Sessions.getCurrent().removeAttribute(TRANSFER);
					
					main_check_container.setVisible(false);
					r1.setVisible(true); 
					
				}
				else {
					statusLabel.setValue("Something went wrong!");
					check_mpin.setVisible(false);
					emptyField();
				}
				
			}
		
			else {
				Messagebox.show("Incorrect Password!");
				check_mpin.setVisible(false);
				emptyField();
				
			}
			}
				
		}
		
		public void emptyField() {
			toAccount.setValue("");
			amountBox.setValue(null);
			transferType.setSelectedItem(null);
			remarks.setValue("");
			newAmountBox.setValue(null);
			fromName.setValue("");
			toName.setValue("");
			submitBtn.setLabel("Pay");
			
			
		}
		
		@Listen("onClick=#close")
		public void close() {
			r_window.setVisible(false);
		    r1.setVisible(false);        
		    emptyField();
			
		}
		
		@Listen("onClick=#r1")
		public void showReceipt() {
		    r_window.setVisible(true);  
		}

}
