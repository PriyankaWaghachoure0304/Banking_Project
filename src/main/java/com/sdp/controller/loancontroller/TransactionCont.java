package com.sdp.controller.loancontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sdp.connection.DbConnection;
/**
 * TransactionCont handles loan disbursement transactions between accounts.
 * <p>
 * It performs the following operations atomically:
 * <ul>
 *   <li>Deducts the amount from the sender account.</li>
 *   <li>Adds the amount to the receiver account.</li>
 *   <li>Logs the transaction in the account_transaction table.</li>
 *   <li>Commits the transaction if all steps succeed, otherwise rolls back.</li>
 * </ul>
 * </p>
 */
public class TransactionCont {
	
	DbConnection db=new DbConnection();
	ResultSet rs;
	 
	  public boolean disburseLoan(String fromAccount, String toAccount, double amount,double balance) {

	        try(Connection con=db.getConnection();
	        		PreparedStatement ps1 = con.prepareStatement("UPDATE account_transaction SET amount = amount - ? WHERE from_account = ?");
	        		PreparedStatement ps2 = con.prepareStatement("UPDATE account_transaction SET amount = amount + ? WHERE to_account = ?");
	        		 PreparedStatement ps3 = con.prepareStatement("SELECT amount FROM account_transaction WHERE to_account = ?");
	        		 PreparedStatement psLog = con.prepareStatement(
	     	                "INSERT INTO account_transaction (from_account, to_account, amount, remark, available_balance) VALUES (?, ?, ?, ?, ?)");
	        		){
	           
	            con.setAutoCommit(false);

	            
	            ps1.setDouble(1, amount);
	            ps1.setString(2, fromAccount);
	            int updateFrom = ps1.executeUpdate();

	            
	            ps2.setDouble(1, amount);
	            ps2.setString(2, toAccount);
	            int updateTo = ps2.executeUpdate();

	          
	            ps3.setString(1, toAccount);
	             rs = ps3.executeQuery();
	             
	             psLog.setString(1, toAccount);
	             rs = psLog.executeQuery();
	             if (rs.next()) {
	                 rs.getDouble("balance");
	             }
	            
	            psLog.setString(1, fromAccount);
	            psLog.setString(2, toAccount);
	            psLog.setDouble(3, amount);
	            psLog.setString(4, "Loan Disbursed");
	            psLog.setDouble(5, balance);
	            int log = psLog.executeUpdate();
	            
	            if (updateFrom == 1 && updateTo == 1 && log == 1) {
	                con.commit();
	                return true;
	            } else {
	                con.rollback();
	                return false;
	            }
	            }
	        catch (Exception e) {
	
	            e.printStackTrace();
	            return false;
	        }
	    }
}
