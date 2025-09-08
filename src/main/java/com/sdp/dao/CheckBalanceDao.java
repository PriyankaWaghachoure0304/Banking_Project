package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.core.Logger;
import org.zkoss.zul.Messagebox;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.connection.DbConnection;
import com.sdp.transaction.TransferDao;

public class CheckBalanceDao {
	
	
	
	DbConnection db = new DbConnection();
	Logger logger = LoggerExample.getLogger();

	private static final String SELECT = "SELECT balance from accounts where account_number=?";
	
	public String[] checkBalance(String accoutNumber) {
		logger.debug("Enter in checkBalance()");
		try (Connection con = db.getConnection();
				PreparedStatement ps = con.prepareStatement(SELECT);) {
			ps.setString(1, accoutNumber);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				String[] data  = new String[3];
				
				data[0]=accoutNumber;
				data[1] = TransferDao.getTransferDao().getName(accoutNumber);
				data[2] = rs.getDouble("balance")+"";
				logger.debug("Exit from checkBalance()");
				return data;
			}
			else {
				Messagebox.show("Account doesn't exist!");
				logger.warn("Account doesn't exist!!!");
			}
			
		}
	
		catch (Exception e) {
			e.printStackTrace();
		}
		return new String[0];
		
	}

}
