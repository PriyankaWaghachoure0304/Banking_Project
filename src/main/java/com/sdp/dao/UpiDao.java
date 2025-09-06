package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sdp.connection.DbConnection;
import com.sdp.model.DebitCardPojo;

public class UpiDao {

	DbConnection db = new DbConnection();

	DebitCardDao dao = new DebitCardDao();

	public boolean validatecard(String accountNumber, String cardnum, int pin) {

		DebitCardPojo card = dao.fetchDebitcard(accountNumber);

		String sixdigit = "";
		for (int i = 10; i <= 15; i++) {
			sixdigit += card.getCardNumber().charAt(i);
		}

		
		return cardnum.equals(sixdigit) && pin == card.getPin();
	}

	private static final String UPDATE_UPI = "Update accounts set upiId=? where account_number=? ;";

	public String generateUpi(String accountnum) {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_UPI)) {
			String email = getMail(accountnum);
			String[] before = email.split("@");
			String upiId = before[0] + "@oksbi";
			ps.setString(1, upiId);
			ps.setString(2, accountnum);
			int rs = ps.executeUpdate();
			if (rs > 0) {
				return upiId;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static final String FETCHEMAIL = "select email from users where account_number=?;";

	public String getMail(String accountnum) {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCHEMAIL)) {
			ps.setString(1, accountnum);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("email");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static final String FETCHEACCOUNT = "select account_number from accounts where upiId=?;";

	public String getAccountNum(String upiid) {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCHEACCOUNT)) {
			ps.setString(1, upiid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("account_number");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public void sendEmail(String toEmail, String upi) throws MessagingException {

		String host = "smtp.gmail.com";
		String from = "sanvika54321@gmail.com";
		String password = "lrpk bqms wpyl mard";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});

		String subject = "Welcome to Kolkata Bank – Your Account Has Been Successfully Created";

		String emailBody = "Dear Customer,\n\n" +
			    "Thank you for choosing Kolkata Bank of India.\n\n" +
			    "Below are your account details:\n" +
			    "UPI ID: " + upi + "\n\n" +
			    "Please keep this information confidential and do not share it with anyone.\n\n" +
			    "If you have any questions or require further assistance, please do not hesitate to contact our Customer Support Team.\n\n" +
			    "Sincerely,\n" +
			    "Kolkata Bank of India\n" +
			    "Customer Service Team";


		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject(subject);
		message.setText(emailBody);

		Transport.send(message);
	}
	private static final String FETCH_UPIID="select upiId from accounts where account_number=?";
	 public String fetchUpiId(String accountnumber) {
		 try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCH_UPIID)) {
				ps.setString(1, accountnumber);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getString("upiId");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	 }
}
