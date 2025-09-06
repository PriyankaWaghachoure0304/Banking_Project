package com.sdp.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Messagebox;

import com.sdp.connection.DbConnection;
import com.sdp.model.Notification;
import com.sdp.model.Receipt;
import com.sdp.serialization.Deserialize;
import com.sdp.serialization.Serialize;

public class TransferDao {

	DbConnection db = new DbConnection();

	private static final String GET_NAME = "SELECT first_name, middle_name, last_name FROM personal_details where account_no=?";
	private static final String FETCH_BALANCE = "SELECT balance FROM accounts WHERE account_number = ?";
	private static final String UPDATE_BALANCE = "UPDATE accounts SET balance = ? WHERE account_number = ?";
	private static final String INSERT_TRANSACTION = "INSERT INTO transactions (transaction_id, from_account, to_account, amount, transaction_type, tax, remark, available_balance, time, from_direction, to_direction) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

	
	private static final String SELECT_MPIN = "SELECT mpin FROM accounts where account_number=?";
	private static final String BALANCE="balance";
	
	public static TransferDao  transferdao =null;
	
	
	public static TransferDao getTransferDao() {
		if(transferdao==null) {
			transferdao=new TransferDao();
			return transferdao;
		}
		else {
			return transferdao;
		}
	}
	
	
	public boolean transferAmount(String fromAcc, String toAcc, double amount, String type, double tax, String remark) {
		double totalDebit = amount + tax;

		try (Connection con = db.getConnection();
				PreparedStatement fetchFrom = con.prepareStatement(FETCH_BALANCE);
				PreparedStatement fetchTo = con.prepareStatement(FETCH_BALANCE);
				PreparedStatement updateFrom = con.prepareStatement(UPDATE_BALANCE);
				PreparedStatement updateTo = con.prepareStatement(UPDATE_BALANCE);
				PreparedStatement insertTxn = con.prepareStatement(INSERT_TRANSACTION)) {

			fetchFrom.setString(1, fromAcc);
			ResultSet rsFrom = fetchFrom.executeQuery();
			if (!rsFrom.next()) {
				Messagebox.show("Sender account does not exist.");
				return false;
			}

			double fromBalance = rsFrom.getDouble(BALANCE);
			if (fromBalance < totalDebit) {
				Messagebox.show("Insufficient funds.");
				return false;
			}

			fetchTo.setString(1, toAcc);
			ResultSet rsTo = fetchTo.executeQuery();
			if (!rsTo.next()) {
				Messagebox.show("Recipient account does not exist.");
				return false;
			}

			double toBalance = rsTo.getDouble(BALANCE);

			double newFromBalance = fromBalance - totalDebit;
			double newToBalance = toBalance + amount;

			updateFrom.setDouble(1, newFromBalance);
			updateFrom.setString(2, fromAcc);

			updateTo.setDouble(1, newToBalance);
			updateTo.setString(2, toAcc);

			String tId = getTransactionId();
			insertTxn.setString(1, tId);
			insertTxn.setString(2, fromAcc);
			insertTxn.setString(3, toAcc);
			insertTxn.setDouble(4, amount);
			insertTxn.setString(5, type);
			insertTxn.setDouble(6, tax);
			insertTxn.setString(7, remark);
			insertTxn.setDouble(8, newFromBalance);
			
			LocalDateTime timestamp = LocalDateTime.now();
			
			insertTxn.setObject(9, timestamp);
			insertTxn.setString(10, "Debited");
			insertTxn.setString(11, "Credited");
			
			
			con.setAutoCommit(false);
			int u1 = updateFrom.executeUpdate();
			int u2 = updateTo.executeUpdate();
			int u3 = insertTxn.executeUpdate();

			if (u1 > 0 && u2 > 0 && u3 > 0) {
				con.commit();
				
				Messagebox.show("Payment successful");
				
				sendNotificationOnTransaction(fromAcc, "₹"+amount+" debited from your account!");
				sendNotificationOnTransaction(toAcc, "₹"+amount+" credited to your account!");
				
				Session session = Sessions.getCurrent();
				session.setAttribute(fromAcc+"last",tId);
				return true;
				
			} else {
				con.rollback();
				Messagebox.show("Transfer failed. Please try again.");
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Error: " + e.getMessage());
			return false;
		}
	}

	public String getName(String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(GET_NAME)) {
			ps.setString(1, accountNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name");
			} else {
				return "Account Number not Valid!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	
	public int isExistMpin(String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_MPIN)) {
			ps.setString(1, accountNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("mpin");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}
	
	
	

	private static final String SELECT = "SELECT transaction_id, from_account, to_account, amount, transaction_type, tax, remark, available_balance, time, from_direction from transactions where transaction_id = ?";

	Receipt receipt = new Receipt();
	
	public Receipt getReceipt(String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement st = con.prepareStatement(SELECT);) {

			String tId1 = (String) Sessions.getCurrent().getAttribute(accountNumber+"last");
			st.setString(1, tId1);
			Sessions.getCurrent().removeAttribute(accountNumber+"last");
			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				String tId = rs.getString("transaction_id");
				String from = rs.getString("from_account");
				String to = rs.getString("to_account");
				Double amount = rs.getDouble("amount");
				String tType = rs.getString("transaction_type");
				Double tax = rs.getDouble("tax");
				String remark = rs.getString("remark");
				Double availableBalance = rs.getDouble("available_balance");
				LocalDateTime time = rs.getObject("time", LocalDateTime.class);
				String fromDirection = rs.getString("from_direction");
				
				receipt.setTransactionId(tId);
				receipt.setFromAccount(from);
				receipt.setToAccount(to);
				receipt.setAmount(amount);
				receipt.setTransactionType(tType);
				receipt.setTax(tax);
				receipt.setRemark(remark);
				receipt.setAvailableBalance(availableBalance);
				receipt.setTime(time);
				receipt.setFromDirection(fromDirection);
				
				return receipt;
				
			}
			

		} catch (Exception e) {

			e.printStackTrace();
		}
		return receipt;
		
		

	}
	
	
	
	public String getTransactionId() {
	    String prefix = "TXN";
	    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); 
	    String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
	    return prefix + timestamp + random;
	}
	
	
	public void b2btransferAmount(String fromAcc, String toAcc, double amount, String type, double tax, String remark) {
		double totalDebit = amount + tax;

		try (Connection con = db.getConnection();
				PreparedStatement fetchFrom = con.prepareStatement(FETCH_BALANCE);
				PreparedStatement updateFrom = con.prepareStatement(UPDATE_BALANCE))
		{

			fetchFrom.setString(1, fromAcc);
			ResultSet rsFrom = fetchFrom.executeQuery();
			if (!rsFrom.next()) {
				Messagebox.show("Sender account does not exist.");
				return;
			}

			double fromBalance = rsFrom.getDouble(BALANCE);
			if (fromBalance < totalDebit) {
				Messagebox.show("Insufficient funds.");
				return;
			}

			
			double newFromBalance = fromBalance - totalDebit;

			updateFrom.setDouble(1, newFromBalance);
			updateFrom.setString(2, fromAcc);

			
			
			con.setAutoCommit(false);
			int u1 = updateFrom.executeUpdate();

			if (u1 > 0 ) {
				con.commit();
				Messagebox.show("Transfer successful.");
				
				
				
			} else {
				con.rollback();
				Messagebox.show("Transfer failed. Please try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Messagebox.show("Error: " + e.getMessage());
		}
	}
	
	
	public void sendNotificationOnTransaction(String accountNo, String message) {

        EventQueue<Event> q = EventQueues.lookup("notification", EventQueues.APPLICATION, true);
        Event e1 = new Event("msg", null, "Notification Alert!");
        q.publish(e1);


        List<Notification> list = new Deserialize().loadMessage();

        for(Notification n : list) {
        }
        
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
