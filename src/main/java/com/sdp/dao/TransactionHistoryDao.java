package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.sdp.connection.DbConnection;
import com.sdp.model.TransferPojo;

public class TransactionHistoryDao {
	
    DbConnection db = new DbConnection();

    private static final String FETCH_TRANSACTION =
    	    "SELECT * FROM transactions WHERE (from_account = ? OR to_account = ?) AND time BETWEEN ? AND ? ORDER BY time DESC";


    public List<TransferPojo> fetchHistory(String accounnum, java.util.Date fromDate, java.util.Date toDate) {
        List<TransferPojo> tList = new ArrayList<>();
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(FETCH_TRANSACTION)) {
            
            ps.setString(1, accounnum);
            ps.setString(2, accounnum); 
            ps.setDate(3, new java.sql.Date(fromDate.getTime()));
            ps.setDate(4, new java.sql.Date(toDate.getTime()));

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TransferPojo p = new TransferPojo();
                p.setFromAccount(rs.getString("from_account"));
                p.setToAccount(rs.getString("to_account"));
                p.setAmount(rs.getDouble("amount"));
                p.setTransferType( rs.getString("transaction_type"));
                p.setRemark(rs.getString("remark"));
                p.setDateTime(rs.getTimestamp("time"));
                p.setFrom_direction(accounnum.equals(rs.getString("from_account")) ? "DEBIT" : "CREDIT");
                p.setCharges(rs.getDouble("tax"));
                tList.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tList;
    }
}
