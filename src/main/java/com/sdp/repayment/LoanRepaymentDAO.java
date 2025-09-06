
package com.sdp.repayment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

import com.sdp.connection.DbConnection;
import com.sdp.customer.AccountOpening;
import com.sdp.model.Notification;
import com.sdp.serialization.Deserialize;
import com.sdp.serialization.Serialize;
import com.sdp.transaction.TransferDao;


public class LoanRepaymentDAO {
DbConnection db=new DbConnection();
	private static final String SQL_SELECT = "SELECT la.account_number, la.emi, lr.remaining_balance, lr.total_paid "
	        + "FROM loan_application la "
	        + "LEFT JOIN loan_repayment lr ON la.account_number = lr.account_number "
	        + "WHERE la.account_number=?";


	public void processEMI(String accountNo) {
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

			ps.setString(1, accountNo);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String accountNum = rs.getString("account_number");
				double emiAmount = rs.getDouble("emi");
				double remainingBalance = rs.getDouble("remaining_balance");
				rs.getDouble("total_paid");

				LocalDateTime currentDueDate;
				try {
					currentDueDate = rs.getTimestamp("next_due_date") != null
							? rs.getTimestamp("next_due_date").toLocalDateTime()
							: LocalDateTime.now();
				} catch (Exception e) {
					currentDueDate = LocalDateTime.now();
				}

				LocalDateTime nextDueDate = currentDueDate.plusMinutes(1);

				if (rs.wasNull()) {
					double loanAmount = getLoanAmount(accountNum, conn);

					String insertRepayment = "INSERT INTO loan_repayment(account_number, total_paid, remaining_balance,next_due_date, updated_at) "
							+ "VALUES(?, ?, ?,?, NOW())";

					try (PreparedStatement psInsert = conn.prepareStatement(insertRepayment)) {
						psInsert.setString(1, accountNum);
						psInsert.setDouble(2, emiAmount);
						psInsert.setDouble(3, loanAmount - emiAmount);
						
						psInsert.setTimestamp(4, Timestamp.valueOf(nextDueDate));
						psInsert.executeUpdate();
					}
				} else {
					
					if (remainingBalance <= 0) {
						return;
					}

					String updateBalance = "UPDATE accounts SET balance = balance - ? WHERE account_number=?";
					try (PreparedStatement ps2 = conn.prepareStatement(updateBalance)) {
						ps2.setDouble(1, emiAmount);
						ps2.setString(2, accountNum);
						ps2.executeUpdate();
					}

					String updateBank = "UPDATE bank_account SET balance_after = balance_after + ? WHERE account_number=?";
			        try (PreparedStatement ps2 = conn.prepareStatement(updateBank)) {
			            ps2.setDouble(1, emiAmount);
			            ps2.setString(2, "SDP00001");
			            ps2.executeUpdate();
			        }
			        
					double newRemaining = remainingBalance - emiAmount;
					if (newRemaining < 0) {
						newRemaining = 0;
					}

					String updateRepayment = "UPDATE loan_repayment "
							+ "SET total_paid = total_paid + ?, remaining_balance = ?,next_due_date=?, updated_at = NOW() "
							+ "WHERE account_number=?";
					try (PreparedStatement ps3 = conn.prepareStatement(updateRepayment)) {
						ps3.setDouble(1, emiAmount);
						ps3.setDouble(2, newRemaining);
						ps3.setTimestamp(3, Timestamp.valueOf(nextDueDate));
						ps3.setString(4, accountNum);
						ps3.executeUpdate();
						
						
						
						
						TransferDao tdao = TransferDao.getTransferDao();
						String tId = tdao.getTransactionId();
						/** 5. Record transaction*/
						try (Connection con1 = new DbConnection().getConnection();
								PreparedStatement ps1  = con1.prepareStatement(
								"INSERT INTO transactions (transaction_id,from_account, to_account, amount,transaction_type, remark, available_balance, time, from_direction, to_direction) VALUES (?,?,?, ?, ?, ?, ?, NOW(),?,?)");
								PreparedStatement ps2 = con1.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?")) {
							
							ps2.setString(1, accountNo);
							ResultSet rr = ps2.executeQuery();
							double availableBalance = 0 ;
							if(rr.next()) {
								availableBalance= rr.getDouble("balance");
							}
							
							ps1.setString(1, tId);
							ps1.setString(2, accountNo);
							ps1.setString(3, "SDP00001" );
							ps1.setDouble(4, emiAmount);
							ps1.setString(5, "Repayment");
							ps1.setString(6, "Loan repayment");
							ps1.setDouble(7, availableBalance);
							ps1.setString(8, "Debited");
							ps1.setString(9, "Credited");
							if(ps1.executeUpdate()>0) {
								String message = "Loan EMI Debited of ₹"+emiAmount;

						        EventQueue<Event> q = EventQueues.lookup("notification", EventQueues.APPLICATION, true);
						        Event e1 = new Event("msg", null, "Notification Alert!");
						        q.publish(e1);


						        List<Notification> list = new Deserialize().loadMessage();

						        Notification notification = new Notification();
						        notification.setAccountNumber(accountNo);
						        notification.setMessage(message);
						        notification.setIsSeen(false);
						        notification.setDateTime(LocalDateTime.now().toLocalDate() + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
						        
						        
						        list.add(notification);

						        Serialize serialize = new Serialize();
						        serialize.saveMessage(list);

							}
						}
						
						sendDisbursedMail(accountNo,emiAmount,newRemaining,nextDueDate);
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendDisbursedMail(String accountNo, double emiAmount, double newRemaining, LocalDateTime nextDueDate) throws SQLException {
		try(
		Connection conn=new DbConnection().getConnection();
		PreparedStatement ps=conn.prepareStatement("Select email from loan_application where account_number=?");){
		ps.setString(1, accountNo);
		ResultSet rs=ps.executeQuery();
		String email = null;
		if(rs.next())
		{
		email=rs.getString("email");
		}
		String	content ="Dear Customer " + " " + accountNo + ",\n\n" + "You have paid total ₹: '" + emiAmount
				+ "\n\n" + "Remaining Balance ₹ " + newRemaining + "\n\n"+" Your Next due date is" + nextDueDate
				+ "\n\n"+ "Our team will contact you shortly for disbursement details.\n\n"
				+ "Thank you for banking with us.\n\n" + "Regards,\nLoan Department";	
		
		new AccountOpening().sendEmail(email, content);
		}
	}


	private double getLoanAmount(String acNo, Connection conn)  {
		String sql = "SELECT loan_amount FROM loan_application WHERE account_number=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, acNo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("loan_amount");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
