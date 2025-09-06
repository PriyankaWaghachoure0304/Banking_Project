package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.zkoss.zk.ui.Sessions;

import com.sdp.connection.DbConnection;
import com.sdp.model.User;

public class LoginDao {
	DbConnection db = new DbConnection();
	
	private static final String FETCHIMAGE= "select photo_path from address_and_documents where account_no = ?";
	public byte[] fetchImage(String accno) {
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCHIMAGE)){
			ps.setString(1, accno);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getBytes("photo_path");
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	public boolean validateLogin(int s1, String s2) {
		try(Connection con = db.getConnection();PreparedStatement pstmt = con
				.prepareStatement("select full_name,account_number,email,phone_number from users where customer_id=? and password = ?")) {
			pstmt.setInt(1, s1);
			pstmt.setString(2, s2);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
					String fullName = rs.getString("full_name");
					String accountnum = rs.getString("account_number");
					String email = rs.getString("email");
					String phone = rs.getString("phone_number");

					User user = new User(fullName, email, phone, s2, accountnum, s1);
					
					
					Sessions.getCurrent().setAttribute("isLoggedIn", true);
					Sessions.getCurrent().setAttribute("userDetails", user);
					Sessions.getCurrent().setAttribute("accountnumber", accountnum);

					return true;
					

				} else {
					return false;
				}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
