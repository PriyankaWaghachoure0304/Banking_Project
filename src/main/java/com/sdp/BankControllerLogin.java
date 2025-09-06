package com.sdp;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class BankControllerLogin extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

	@Wire
    Menuitem log1;

    @Wire
    Menuitem logout;
    
    @Wire
    Label usernamelogin;
    
    @Wire
    Menuitem newAccountOpenform;
    
    @Wire
    Menuitem deposit;
    @Wire Menuitem  transfer;
    @Wire Menuitem  cards;
    
    
   transient Execution exec = org.zkoss.zk.ui.Executions.getCurrent();
	
    Boolean isLoggedIn = (Boolean) Sessions.getCurrent().getAttribute("isLoggedIn");
    String accountnum = (String) Sessions.getCurrent().getAttribute("accountnumber");
   
    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        if (isLoggedIn != null && isLoggedIn) {
            log1.setVisible(false);
            
            String username = (String) Sessions.getCurrent().getAttribute("username");
            if (username != null) {
            	usernamelogin.setValue("Welcome : " + username+" "+ ", Account Number Is : "+ accountnum);
            	
            }
            logout.setVisible(true);
        } else {
            log1.setVisible(true);
            logout.setVisible(false);
            usernamelogin.setValue("");
            
        }
    }

    @Listen("onClick = #log1")
    public void goToLogin() {
        Executions.getCurrent().sendRedirect("LoginPage.zul");
        
    }

    @Listen("onClick = #logout")
    public void logoutUser() {
        Sessions.getCurrent().invalidate();
        
        Executions.getCurrent().sendRedirect("bank_Interface.zul");
        alert("You are Logged Out");
    }
    

    @Listen("onClick = #newAccountOpenform")
    public void handleAccountOpenClick() {

    	Executions.getCurrent().sendRedirect("Account_Opening_from.zul");
            
    }
    
    
    
    
    @Listen("onClick = #deposit")
    public void depositClick() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/DipositAmount/depositAmount.zul");
            
        } else {
            Messagebox.show("Please login first....");
        }
    }
    
    @Listen("onClick = #transfer")
    public void transferClick() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/transactions/transfer.zul");
            
        } else {
            Messagebox.show("Please login first..");
        }
    }
    
    @Listen("onClick = #creatempin")
    public void creatempin() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/MPin/mPINcreate.zul");
            
        } else {
            Messagebox.show("Please login first...");
        }
    }
    
    
    @Listen("onClick=#cards")
    public void cards() {
    	 if (isLoggedIn != null && isLoggedIn) {
             Executions.getCurrent().sendRedirect("/cards/cards.zul");
             
         } else {
             Messagebox.show("Please login first....");
         }
    }
    
    @Listen("onClick=#checkbalance")
    public void checkBalance() {
    	Executions.sendRedirect("/CheckBalance/checkBalance.zul");
    }
    
    @Listen("onClick = #b2btransfer")
    public void b2btranfer() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/bank-to-bank-Transfer/bank_transfer.zul");
            
        } else {
            Messagebox.show("Please login first...");
        }
    }
    
    @Listen("onClick = #history")
    public void transactionhistory() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/history/transaction_history.zul");
            
        } else {
            Messagebox.show("Please login first..");
        }
    }
    
    @Listen("onClick = #upi")
    public void createUPIid() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/UPI/upi_id.zul");
            
        } else {
            Messagebox.show("Please login first.......");
        }
    }
    

    @Listen("onClick = #myqr")
    public void myqr() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/Qr/qrcode.zul");
            
        } else {
            Messagebox.show("Please login first.....");
        }
    }
    
    @Listen("onClick = #scan")
    public void scantopay() {

        if (isLoggedIn != null && isLoggedIn) {
            Executions.getCurrent().sendRedirect("/Qr/scanner.zul");
            
        } else {
            Messagebox.show("Please login first.....");
        }
    }
    
}
