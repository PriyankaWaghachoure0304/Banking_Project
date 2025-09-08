package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;

import com.sdp.connection.DbConnection;

public class CraditCardGenarateDao {

    private static final Random random = new Random();

    /**
     * 
     * Step 1: Set the first digit(s) as prefix (e.g., '4' for VISA, '5' for
     * MasterCard)
     * 
     **/
    public String genCreditCardNumber() {
        int[] digits = new int[16];

        digits[0] = 4;

        for (int i = 1; i < 15; i++) {
            digits[i] = random.nextInt(10); 
        }

        int sum = 0; 
        for (int i = 0; i < 15; i++) {
            int digit = digits[14 - i];
            if (i % 2 == 0) { 
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        digits[15] = (10 - (sum % 10)) % 10; 

        StringBuilder cardNumber = new StringBuilder();
        for (int d : digits) {
            cardNumber.append(d);
        }

        return cardNumber.toString();
    }

    DbConnection db = new DbConnection();
    
    private static final String INSERTCARDQUERY="insert into credit_cards (account_number,card_number,cardholder_name,limit_amount) values (?,?,?,?);";
    
    public void insertCreditCard(String accNo, String cardNo, String cardHolderName, double limit) {
    	
    	try(Connection con = db.getConnection();PreparedStatement ps = con.prepareStatement(INSERTCARDQUERY);){
    		ps.setString(1, accNo);
    		ps.setString(2, cardNo);
    		ps.setString(3, cardHolderName);
    		ps.setDouble(4, limit);
    		ps.executeUpdate();
    	}
    	catch (Exception e) { 
    		e.printStackTrace();
		}
    }
    private static final String FETCHFULLNAMEQUERY="select full_name from users where account_number=?;";
    public String getFullNameUser(String accno) {
    	try(Connection con = db.getConnection();PreparedStatement ps = con.prepareStatement(FETCHFULLNAMEQUERY);){
    		ps.setString(1, accno);
    		
    		ResultSet rs = ps.executeQuery();
    		if(rs.next()) {
    		
    		return rs.getString("full_name");
    		}
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return null;
		}
		return null;
    }
    
   
}
