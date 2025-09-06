package com.sdp.controller.loancontroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import com.sdp.connection.DbConnection;
import com.sdp.customer.Customer;
import com.sdp.repayment.LoanRepaymentScheduler;
import com.sdp.transaction.TransferDao;

/**
 * LoanDisbursementController handles the disbursement of approved loans.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Loading approved loans into the Listbox.</li>
 *   <li>Processing loan disbursement, including transferring funds from bank to user's account.</li>
 *   <li>Inserting the initial repayment schedule.</li>
 *   <li>Updating loan status to "Disbursed".</li>
 *   <li>Recording transactions in account_transaction table.</li>
 *   <li>Starting repayment scheduler for automated EMIs.</li>
 * </ul>
 * </p>
 */
public class LoanDisbursementController extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	@Wire
	Listbox l1;

	private static final String FROM_ACCOUNT = "SDP00001";

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
		loadLoans();
	}
/**
 * Loads all approved loans from the database and populates the Listbox.
 */
	DbConnection db=new DbConnection();
	private void loadLoans() throws SQLException {
		List<Customer> loans = new ArrayList<>();
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"SELECT name,purpose,loan_amount,loan_AccountNo,created_at,account_number FROM loan_application WHERE status = 'Approved'");
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Customer cust = new Customer();
				cust.setName(rs.getString("name"));
				cust.setPurpose(rs.getString("purpose"));
				cust.setLoanAmount(rs.getBigDecimal("loan_amount"));
				cust.setAccountNumber(rs.getString("account_number"));
				cust.setCreatedAt(rs.getTimestamp("created_at"));
				cust.setStatus("Approved");
				loans.add(cust);
			}
			l1.setModel(new ListModelList<>(loans));
		}
	}
	
	
	
	/**
	 * Handles the click event for disbursing a loan.
	 */
	String sql = "UPDATE loan_application SET status = 'Disbursed' WHERE account_number = ?";

	 @Listen("onClick = button")
	    public void disburseLoan(Event event) throws SQLException{
	        Button btn = (Button) event.getTarget();
	        Listitem row = (Listitem) btn.getParent().getParent();
	        Customer selectedLoan = row.getValue();
	        
	        String toAccount = selectedLoan.getAccountNumber();
	        BigDecimal amount = selectedLoan.getLoanAmount();
	        String remark = "Disbursed";

	        Sessions.getCurrent().setAttribute("userAccountNo", toAccount);

	        boolean success = transferAmount(FROM_ACCOUNT, toAccount, amount, remark);

	        if (success) {
	            updateLoanStatus(selectedLoan.getAccountNumber());
	            insertInitialRepayment(selectedLoan.getAccountNumber()); 
	            Messagebox.show("Loan disbursed successfully!");
	            try (Connection conn = db.getConnection();
	                    PreparedStatement ps = conn.prepareStatement(sql)) {
	                   ps.setString(1, toAccount);
	                   ps.executeUpdate();
	               } catch (Exception e) {
	                   e.printStackTrace();
	               }
	            
	            String accountNo = selectedLoan.getAccountNumber();
	           
	            LoanRepaymentScheduler scheduler = new LoanRepaymentScheduler(accountNo);
	            scheduler.start();
	            loadLoans();
	        } else {
	            Messagebox.show("Disbursement failed. Insufficient balance or internal error.");
	        }
	    }
	 
	 
	


	private void insertInitialRepayment(String accountNumber) {
		    try (Connection conn = db.getConnection();
		         PreparedStatement ps = conn.prepareStatement(
		        		 "INSERT INTO loan_repayment (account_number, total_paid, remaining_balance, next_due_date) " +
		        				 "SELECT la.account_number, 0.00, la.loan_amount, DATE_ADD(NOW(), INTERVAL 1 MINUTE) " +
		        				 "FROM loan_application la " +
		        				 "WHERE la.account_number = ? " +
		        				 "AND NOT EXISTS (SELECT 1 FROM loan_repayment lr WHERE lr.account_number = la.account_number)"
)) {

		        ps.setString(1, accountNumber);
		        ps.executeUpdate();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}

	private boolean transferAmount(String fromAccount, String toAccount, BigDecimal amount, String remark) {
		try (Connection conn = db.getConnection()) {
			conn.setAutoCommit(false);

			/** 1. Check bank balance*/
			BigDecimal bankBalance = BigDecimal.ZERO;
			try (PreparedStatement ps = conn
					.prepareStatement("SELECT balance_after FROM bank_account WHERE account_number = ?")) {
				ps.setString(1, fromAccount);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						bankBalance = rs.getBigDecimal("balance_after");
					}
				}
			}

			if (bankBalance.compareTo(amount) < 0) {
				return false;
			}

			/** 2. Deduct from bank_account*/
			try (PreparedStatement ps = conn
					.prepareStatement("UPDATE bank_account SET balance_after = balance_after - ? WHERE account_number = ?")) {
				ps.setBigDecimal(1, amount);
				ps.setString(2, fromAccount);
				ps.executeUpdate();
			}

			/** 3. Credit to user_BankAccount*/
			try (PreparedStatement ps = conn
					.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?")) {
				ps.setBigDecimal(1, amount);
				ps.setString(2, toAccount);
				ps.executeUpdate();
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			/** 4. Get updated balance*/
			BigDecimal updatedBalance = BigDecimal.ZERO;
			try (PreparedStatement ps = conn
					.prepareStatement("SELECT balance_after FROM bank_account WHERE account_number = ?")) {
				ps.setString(1, fromAccount);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						updatedBalance = rs.getBigDecimal("balance_after");
					}
				}
			}
			TransferDao tdao = TransferDao.getTransferDao();
			String tId = tdao.getTransactionId();
			/** 5. Record transaction*/
			try (PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO transactions (transaction_id,from_account, to_account, amount,transaction_type, remark, available_balance, time, from_direction, to_direction) VALUES (?,?,?, ?, ?, ?, ?, NOW(),?,?)")) {
				ps.setString(1, tId);
				ps.setString(2, fromAccount);
				ps.setString(3, toAccount);
				ps.setBigDecimal(4, amount);
				ps.setString(5, "Loan");
				ps.setString(6, remark);
				ps.setBigDecimal(7, updatedBalance);
				ps.setString(8, "Debited");
				ps.setString(9, "Credited");
				ps.executeUpdate();
			}

			conn.commit();
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Updates the status of a loan to "Disbursed".
	 */
	private void updateLoanStatus(String accountNo) {
		try (Connection conn = db.getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"UPDATE loan_application SET status = 'Disbursed' WHERE account_number = ?")) {
			ps.setString(1, accountNo);
			ps.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
