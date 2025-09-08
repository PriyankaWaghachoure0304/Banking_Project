package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.core.Logger;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.connection.DbConnection;

public class AdminLoginDao {

	Logger logger = LoggerExample.getLogger();
    public boolean validateAdmin(int adminId, String password) {
    	
    	logger.debug("Enter in validateLogin()");
    	
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
            logger.error("Invalid details"+e);
        }

        logger.debug("Exit from validateLogin()");
        return isValid;
    }
}
