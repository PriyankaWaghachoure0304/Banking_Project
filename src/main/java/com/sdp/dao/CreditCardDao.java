package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Sessions;

import com.sdp.connection.DbConnection;
import com.sdp.model.CreditCard;
import com.sdp.model.CreditCard_apply;

public class CreditCardDao {

	static final String LIMIT="limit_amount";
	DbConnection db=new DbConnection();
	static final String ACCNO="account_number";
	String selectWl = "SELECT full_name,request_date,account_number,limit_amount FROM credit_card_requests WHERE request_status = 'waiting' ";

	public List<CreditCard_apply> fetchWL() throws SQLException {
		ArrayList<CreditCard_apply> al1 = new ArrayList<>();
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(selectWl);) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String accNo=rs.getString(ACCNO);
				String name = rs.getString("full_name");
				Double limit = rs.getDouble(LIMIT);
				Sessions.getCurrent().setAttribute("card_limit", limit);
				Timestamp date = rs.getTimestamp("request_date");
				CreditCard_apply creditcardP = new CreditCard_apply();
				creditcardP.setAccountNumber(accNo);
				creditcardP.setFullName(name);
				creditcardP.setRequestedLimit(limit);
				creditcardP.setCreatedAt(date);
				al1.add(creditcardP);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al1;
	}

	public String getCreditStatus(String accountNo) {
		
		    String status = null;
		    Connection con =db. getConnection();
			try(PreparedStatement ps = con.prepareStatement("SELECT request_status FROM credit_card_requests WHERE account_number=?");) {
				
				ps.setString(1, accountNo);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					status = rs.getString("request_status");
					return status;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		   
	
	}
	
	String selectAlAc="SELECT account_number from credit_card_requests";
	public String fetchAllCustomer()
	{
		ArrayList<CreditCard_apply> al2 = new ArrayList<>();
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(selectAlAc);) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String accNo=rs.getString(ACCNO);
				CreditCard_apply creditcardP = new CreditCard_apply();
				creditcardP.setAccountNumber(accNo);
				al2.add(creditcardP);
				return accNo;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	private static final String UPDATECREDITSTATUS="update credit_card_requests set request_status='approved' where account_number=?";
	public void updateApprove(String ac) {
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATECREDITSTATUS);) {
			ps.setString(1, ac);
			ps.executeUpdate();
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static final String UPDATECREDITSTATUSREJECT="update credit_card_requests set request_status='rejected' where account_number=?";
	public void updateReject(String ac) {
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATECREDITSTATUSREJECT);) {
			ps.setString(1, ac);
		 ps.executeUpdate();

			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final String FETCHCRADITAPPLY="select account_number from credit_card_requests where account_number=?";

	public boolean fetchCraditcardApply(String accno) {
		try(Connection con = db.getConnection();PreparedStatement ps = con.prepareStatement(FETCHCRADITAPPLY)){
			ps.setString(1, accno);
			ResultSet rs = ps.executeQuery();
				return rs.next();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public CreditCard getCreditCardByAccount(String accountNumber) {
        CreditCard card = null;
        
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id,account_number,card_number,cardholder_name,cardholder_name,cvv,exp_month, exp_year,limit_amount  FROM credit_cards WHERE account_number=? LIMIT 1")) {

            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                card = new CreditCard();
                card.setId(rs.getInt("id"));
                card.setAccountNumber(rs.getString(ACCNO));
                card.setCardNumber(rs.getString("card_number"));
                card.setCardholderName(rs.getString("cardholder_name"));
                card.setCvv(rs.getString("cvv"));
                card.setExpMonth(rs.getInt("exp_month"));
                card.setExpYear(rs.getInt("exp_year"));
                card.setLimitAmount(rs.getDouble(LIMIT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return card;
    }
	
	public List<CreditCard_apply> fetchAllApprovedUsers() throws SQLException {
	    List<CreditCard_apply> list = new ArrayList<>();
	    String sql = "SELECT c.account_number, c.card_number, c.cardholder_name, c.limit_amount, c.created_at " +
	                 "FROM credit_cards c";
	    try (Connection conn = db.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            CreditCard_apply obj = new CreditCard_apply();
	            obj.setAccountNumber(rs.getString(ACCNO));
	            obj.setCardNumber(rs.getString("card_number"));
	            obj.setFullName(rs.getString("cardholder_name"));
	            obj.setRequestedLimit(rs.getDouble(LIMIT));
	            obj.setCreatedAt(rs.getDate("created_at"));
	            list.add(obj);
	        }
	    }
	    return list;
	}

}
