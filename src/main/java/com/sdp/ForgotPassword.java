package com.sdp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.connection.DbConnection;

public class ForgotPassword extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;

	@Wire
	Textbox userfor;

	@Wire
	Textbox phone;

	

	DbConnection db = new DbConnection();

	@Listen("onClick = #sendOtp")
	public void sendOpt() throws MessagingException {
  String generatedOtp;
		String userName = userfor.getValue();
		String phoneNumber = phone.getValue();

		try (Connection con = db.getConnection();
				PreparedStatement pstmt = con
						.prepareStatement("select email,phone_number from users where phone_number=? or email=?;");
				PreparedStatement pstmt2 = con.prepareStatement("update users set otp=? where phone_number=?");) {

			pstmt.setString(1, phoneNumber);
			pstmt.setString(2, userName);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String databaseUser = rs.getString("email");
				String phonedb = rs.getString("phone_number");
				if (phonedb.equals(phoneNumber) && databaseUser.equals(userName)) {
					Random random = new Random();
					int r = random.nextInt(10000);
					generatedOtp = String.format("%4d", r);
					Sessions.getCurrent().setAttribute("userOtp", generatedOtp);
					sendEmail(userName, generatedOtp);

					Messagebox.show("Otp Sent Your Registered Email...");

					pstmt2.setString(1, generatedOtp);
					pstmt2.setString(2, phoneNumber);
					pstmt2.executeUpdate();

				} else {
					Messagebox.show("User Name Or Phone Number Does Not Match...");
				}
			} else {
				Messagebox.show("Phone Number Not Registered..");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(String toEmail, String otp) throws MessagingException {

		String host = "smtp.gmail.com";
		String from = "sanvika54321@gmail.com";
		String password1 = "lrpk bqms wpyl mard";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password1);
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		message.setSubject("Your OTP Code");
		message.setText("Your OTP is: " + otp);

		Transport.send(message);
	}

	@Wire
	Textbox newPass;

	@Wire
	Textbox Compass;

	@Wire
	Textbox otp;

	@Listen("onClick = #resetpass")
	public void resetpassword() {
		String newPassword = newPass.getValue();
		String compassword = Compass.getValue();
		String otp1 = otp.getValue();
		String phoneNumber = phone.getValue();

		if (!newPassword.equals(compassword)) {
			Messagebox.show("Passwords do not match.");
			return;
		}

		try (Connection con = db.getConnection();
				PreparedStatement pstmt2 = con
						.prepareStatement("update users set password=?, otp=NULL where phone_number=?");
				PreparedStatement pstmt = con
						.prepareStatement("SELECT phone_number FROM users WHERE phone_number = ? AND otp = ?;");) {

			pstmt.setString(1, phoneNumber);
			pstmt.setString(2, otp1);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				pstmt2.setString(1, compassword);
				pstmt2.setString(2, phoneNumber);
				pstmt2.executeUpdate();

				Messagebox.show("Password Reset Successfully...");

				userfor.setValue("");
				phone.setValue("");
				otp.setValue("");
				newPass.setValue("");
				Compass.setValue("");

			} else {
				Messagebox.show("Invalid OTP...");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
