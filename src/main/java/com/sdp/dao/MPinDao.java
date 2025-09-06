package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sdp.connection.DbConnection;

public class MPinDao {

	DbConnection db = new DbConnection();

	private static final String UPDATEQUERY = "UPDATE accounts SET mpin = ? WHERE account_number=?";

	public int setMPin(int mpin, String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATEQUERY);) {
			ps.setInt(1, mpin);
			ps.setString(2, accountNumber);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
	
	private static final String CHECK = "SELECT mpin FROM accounts WHERE account_number=?";

	public boolean checkMPin(int mpin, String accountNumber) {

		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(CHECK);) {
			ps.setString(1, accountNumber);

			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				int pass = rs.getInt("mpin");
				
					return pass==mpin;
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
}
