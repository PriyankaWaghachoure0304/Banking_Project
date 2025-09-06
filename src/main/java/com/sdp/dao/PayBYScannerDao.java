package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.zkoss.zk.ui.Sessions;

import com.sdp.connection.DbConnection;
import com.sdp.model.Receipt;

public class PayBYScannerDao {

	private static final String FETCH_MONEY = "SELECT balance FROM accounts WHERE account_number = ?";
	private static final String UPDATE_MONEY = "UPDATE accounts SET balance = ? WHERE account_number = ?";
	private static final String INSERT_TRANSACTION = "INSERT INTO transactions (transaction_id, from_account, to_account, amount, transaction_type, tax, remark, available_balance, time, from_direction, to_direction) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String GET_NAME = "SELECT first_name, middle_name, last_name FROM personal_details where account_no=?";
	private final DbConnection db = new DbConnection();
	static final String TRANID = "transacId";

	/**
	 * Performs full UPI transfer inside a single method: - Deducts from sender -
	 * Adds to receiver - Inserts 1 transaction record
	 * 
	 * @return true if everything succeeded, false otherwise
	 */
	public boolean transferByScanner(String fromAcc, String toAcc, double amount, String remark) {
		String txnId = generateTxnId();

		Sessions.getCurrent().setAttribute(TRANID, txnId);

		String type = "UPI";
		double tax = 0.0;
		String fromDirection = "Debited";
		String toDirection = "Credited";
		LocalDateTime timestamp = LocalDateTime.now();

		try (Connection con = db.getConnection()) {
			con.setAutoCommit(false);

			Double fromBalance = getBalance(con, fromAcc);
			Double toBalance = getBalance(con, toAcc);

			if (fromBalance == null || toBalance == null) {
				throw new NullPointerException("One of the accounts does not exist.");
			}

			if (fromBalance < amount) {
				throw new Exception("Insufficient balance in sender's account.");
			}

			double newFromBalance = fromBalance - amount;
			double newToBalance = toBalance + amount;

			boolean fromUpdated = updateBalance(con, fromAcc, newFromBalance);
			boolean toUpdated = updateBalance(con, toAcc, newToBalance);

			boolean txnInserted = insertTransaction(con, txnId, fromAcc, toAcc, amount, type, tax, remark,
					newFromBalance, timestamp, fromDirection, toDirection);

			if (fromUpdated && toUpdated && txnInserted) {
				con.commit();
				return true;
			} else {
				con.rollback();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Double getBalance(Connection con, String accountNumber) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(FETCH_MONEY)) {
			ps.setString(1, accountNumber);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble("balance");
			}
		}
		return null;
	}

	private boolean updateBalance(Connection con, String accountNumber, double newBalance) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(UPDATE_MONEY)) {
			ps.setDouble(1, newBalance);
			ps.setString(2, accountNumber);
			return ps.executeUpdate() > 0;
		}
	}

	private boolean insertTransaction(Connection con, String txnId, String fromAcc, String toAcc, double amount,
			String type, double tax, String remark, double fromBalance, LocalDateTime time, String fromDir,
			String toDir) throws SQLException  {
		try (PreparedStatement ps = con.prepareStatement(INSERT_TRANSACTION)) {
			ps.setString(1, txnId);
			ps.setString(2, fromAcc);
			ps.setString(3, toAcc);
			ps.setDouble(4, amount);
			ps.setString(5, type);
			ps.setDouble(6, tax);
			ps.setString(7, remark);
			ps.setDouble(8, fromBalance);
			ps.setObject(9, time);
			ps.setString(10, fromDir);
			ps.setString(11, toDir);
			return ps.executeUpdate() > 0;
		}
	}

	private String generateTxnId() {
		String prefix = "TXN";
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
		return prefix + timestamp + random;
	}

	public String getName(String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(GET_NAME)) {
			ps.setString(1, accountNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name");
			} else {
				return "Account Number not found!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private static final String SELECT = "SELECT transaction_id, from_account, to_account, amount, transaction_type, tax, remark, available_balance, time, from_direction from transactions where transaction_id = ?";

	Receipt receipt = new Receipt();

	public Receipt getReceipt() {

		try (Connection con = db.getConnection(); PreparedStatement st = con.prepareStatement(SELECT);) {

			String tId1 = (String) Sessions.getCurrent().getAttribute(TRANID);
			st.setString(1, tId1);
			Sessions.getCurrent().removeAttribute(TRANID);
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

	private static final String GET_MPIN = "SELECT mpin FROM accounts WHERE account_number = ?";

	public int getMpin(String accountNumber) {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(GET_MPIN)) {
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

}
