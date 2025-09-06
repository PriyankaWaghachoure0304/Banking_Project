package com.sdp.customer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.sdp.ValidateLogin;
import com.sdp.connection.DbConnection;

public class ValidateSignup extends SelectorComposer<Div> {
	private static final long serialVersionUID = 1L;
	@Wire
	Intbox cusid;
	@Wire
	Textbox newpass;
	@Wire
	Textbox compass;

	DbConnection db = new DbConnection();

	@Listen("onOK=#cusid")
	public void jumpToNewpass() {
		newpass.setFocus(true);
	}

	@Listen("onOK=#newpass")
	public void jumpToConfirmPass() {
		compass.setFocus(true);
	}

	@Listen("onOK=#compass")
	public void jumpToSignUp() {
		passmatch();
	}

	public String digestPassword(String password) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("sha-512");
			md.update(password.getBytes());
			byte[] digestmessage = md.digest();

			StringBuilder hashcode = new StringBuilder();
			for (int i = 0; i < digestmessage.length; i++) {
				hashcode.append(String.format("%02x", digestmessage[i]));
			}
			return hashcode.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Listen("onClick = #b1")
	public void passmatch() {

		int s1 = cusid.getValue();
		String s2 = digestPassword(newpass.getValue());
		String s3 = digestPassword(compass.getValue());

		try (Connection con = db.getConnection();
				PreparedStatement pstmt2 = con.prepareStatement(
						"select full_name,account_number,password,customer_id from users where customer_id=?");) {
			if (s1 == 0) {
				Messagebox.show("Enter Valid Customer Id..");
				return;
			}
			if (s2.equals("") || s3.equals("")) {
				Messagebox.show("Enter Password ");
				return;
			}
			if (!s2.equals(s3)) {
				Messagebox.show("Password Does Not Match.....");
				return;
			}

			pstmt2.setInt(1, s1);
			ResultSet rs1 = pstmt2.executeQuery();

			if (rs1.next()) {
				String pass = rs1.getString("password");

				if (pass != null) {
					Messagebox.show("UserName Already Exist");
					return;
				} else {

					insertPass(s3, s1);
				}

			} else {
				Messagebox.show("Create Account First..");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertPass(String passwo, int cid) {
		try (Connection con = db.getConnection();
				PreparedStatement pstmt = con.prepareStatement("update users set password=? where customer_id=?");
				) {
			
			pstmt.setString(1, passwo);
			pstmt.setInt(2, cid);
			int rs = pstmt.executeUpdate();
			if (rs > 0) {
				Messagebox.show("You Are Successfully Signed Up...");
				cusid.setValue(0000);
				newpass.setValue("");
				compass.setValue("");
				Sessions.getCurrent().setAttribute("isSignedup", true);

				ValidateLogin vLogin = new ValidateLogin();

				vLogin.signUpLogin(cid, passwo);

			} else {
				Messagebox.show("Something Went Wrong..");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
