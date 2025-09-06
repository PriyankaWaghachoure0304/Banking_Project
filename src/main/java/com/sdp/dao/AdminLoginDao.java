package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sdp.connection.DbConnection;

public class AdminLoginDao {

    public boolean validateAdmin(int adminId, String password) {
        boolean isValid = false;

        String sql = "SELECT id FROM admin WHERE id = ? AND password = ?";
        DbConnection db = new DbConnection();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, adminId);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isValid = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isValid;
    }
}
