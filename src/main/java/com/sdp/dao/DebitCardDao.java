package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sdp.connection.DbConnection;
import com.sdp.model.DebitCardPojo;

public class DebitCardDao {
	private static final String SELECTDEBITCARDNO = "select card_holder_name,card_number,valid_exp,cvv,pin from debit_card where account_no = ?";

	DbConnection db = new DbConnection();
	
	public DebitCardPojo fetchDebitcard(String accountno) {
		
		try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(SELECTDEBITCARDNO)) {
			ps.setString(1, accountno);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String cardno = rs.getString("card_number");
				String validExp = rs.getString("valid_exp");
				int cvv = rs.getInt("cvv");
				int cardpin = rs.getInt("pin");
				String cardHolderName = rs.getString("card_holder_name");
				DebitCardPojo dcard = new DebitCardPojo();
				dcard.setCardNumber(cardno);
				dcard.setValidExp(validExp);
				dcard.setCvv(cvv);
				dcard.setPin(cardpin);
				dcard.setCardHolderName(cardHolderName);
				return dcard;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static final String FETCHDEBITCARD = "select account_no,card_holder_name,card_number,valid_exp,cvv,pin from debit_card where card_number = ?";
	public DebitCardPojo fetchDebitcardwithDebitcard(String cardNo) {
			
			try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(FETCHDEBITCARD)) {
				ps.setString(1, cardNo);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					String accNum = rs.getString("account_no");
					String cardno = rs.getString("card_number");
					String validExp = rs.getString("valid_exp");
					int cvv = rs.getInt("cvv");
					int cardpin = rs.getInt("pin");
					String cardHolderName = rs.getString("card_holder_name");
					DebitCardPojo dcard = new DebitCardPojo();
					dcard.setAccountNumber(accNum);
					dcard.setCardNumber(cardno);
					dcard.setValidExp(validExp);
					dcard.setCvv(cvv);
					dcard.setPin(cardpin);
					dcard.setCardHolderName(cardHolderName);
					return dcard;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	
	
	
}
