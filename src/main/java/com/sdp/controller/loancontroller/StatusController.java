package com.sdp.controller.loancontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Messagebox;

import com.sdp.connection.DbConnection;
import com.sdp.customer.AccountOpening;
/**
 * StatusController handles the approval and rejection of loan applications.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Approving a loan and creating a corresponding user bank account.</li>
 *   <li>Rejecting a loan application.</li>
 *   <li>Generating unique bank account numbers for new accounts.</li>
 *   <li>Fetching the loan account number for a given application.</li>
 *   <li>Sending status emails to customers upon approval or rejection.</li>
 * </ul>
 * </p>
 */
public class StatusController {
	AccountOpening ac=new AccountOpening();
	
	DbConnection db=new DbConnection();
	public void approve() {
	    String accNo =  (String) Sessions.getCurrent().getAttribute("AccountNo");
	    if (accNo == null) {
	        Messagebox.show("No application selected.");
	        return;
	    }

	    try (Connection conn = db.getConnection()) {
	        conn.setAutoCommit(false); 

	        /** Update loan application*/
	        String loanUpdate = "UPDATE loan_application SET status = 'Approved' WHERE account_number = ?";
	        try (PreparedStatement ps = conn.prepareStatement(loanUpdate)) {
	            ps.setString(1, accNo);
	            if (ps.executeUpdate() == 0) {
	                Messagebox.show("No loan application found with this ID.");
	                conn.rollback();
	                return;
	            }
	        }

	        

	        conn.commit(); 
	        Messagebox.show("Loan Approved.");
	       ac. sendStatusEmail(accNo, "Approved");

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	String sql = "UPDATE loan_application SET status = 'Rejected' WHERE account_number = ?";
	@Listen("onClick=#bReject")
	public void reject() {
		String accNo = (String) Sessions.getCurrent().getAttribute("AccountNo");
		if (accNo == null) {
			Messagebox.show("No application selected.");
			return;
		}
		try (Connection conn = db.getConnection();PreparedStatement ps = conn.prepareStatement(sql);) {
			
			ps.setString(1, accNo);
			int rows = ps.executeUpdate();
			if (rows > 0) {
				Messagebox.show("Status updated to Rejected.");
				ac.sendStatusEmail(accNo, "Rejected");
			} else {
				Messagebox.show("No record found with this ID.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
