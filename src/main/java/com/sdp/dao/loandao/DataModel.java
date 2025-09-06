package com.sdp.dao.loandao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.RowSetProvider;

import com.sdp.connection.DbConnection;
import com.sdp.controller.loancontroller.PurposeFilter;
import com.sdp.customer.Customer;

public class DataModel implements Serializable {
	private static final long serialVersionUID = 1L;
static final String PURPOSE="purpose";
static final String CREATED="created_at";
static final String ACCNO="account_number";
static final String STATUS="status";
static final String MOBILE="mobile";

DbConnection db=new DbConnection();
	public List<Customer> fetchData() {
		ArrayList<Customer> al = new ArrayList<>();
		try (Connection conn = db.getConnection(); Statement stmt = conn.createStatement();) {
			ResultSet rs = stmt.executeQuery("Select name,purpose,created_at,account_number,status,mobile from loan_application");
			while (rs.next()) {
				String name = rs.getString("name");
				String loanPurpose = rs.getString(PURPOSE);
				Timestamp loanDate = rs.getTimestamp(CREATED);
				String accNo = rs.getString(ACCNO);
				String status = rs.getString(STATUS);
				String mobile = rs.getString(MOBILE);
				Customer s = new Customer();
				s.setName(name);
				s.setPurpose(loanPurpose);
				s.setCreatedAt(loanDate);
				s.setStatus(status);
				s.setMobile(mobile);
				s.setAccountNumber(accNo);
				al.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;

	}

	String selectWl = "SELECT name,purpose,created_at,account_number FROM loan_application WHERE status = 'waiting' ";

	public List<Customer> fetchWL() throws SQLException {
		ArrayList<Customer> al1 = new ArrayList<>();
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(selectWl);) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String accNo=rs.getString(ACCNO);
				String name = rs.getString("name");
				String purpose = rs.getString(PURPOSE);
				Timestamp date = rs.getTimestamp(CREATED);
				Customer s = new Customer();
				s.setAccountNumber(accNo);
				s.setName(name);
				s.setPurpose(purpose);
				s.setCreatedAt(date);
				al1.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al1;
	}

	String selectAl = "SELECT name,purpose,loan_amount,created_at,account_number FROM loan_application WHERE status = 'Approved' ";

	public List<Customer> fetchAL() throws SQLException {
		ArrayList<Customer> al2 = new ArrayList<>();
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(selectAl);) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String ac = rs.getString(ACCNO);
				String name = rs.getString("name");
				String purpose = rs.getString(PURPOSE);
				BigDecimal amount = rs.getBigDecimal("loan_amount");
				Timestamp date = rs.getTimestamp(CREATED);
				Customer s = new Customer();
				s.setAccountNumber(ac);

				s.setName(name);
				s.setPurpose(purpose);
				s.setLoanAmount(amount);
				s.setCreatedAt(date);
				
				al2.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al2;
	}

	String selectDl = "SELECT account_number,name,mobile,purpose,status FROM loan_application WHERE status = 'Disbursed' ";

	public List<Customer> fetchDL() throws SQLException {
		ArrayList<Customer> al3 = new ArrayList<>();
		try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(selectDl);) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String acno = rs.getString(ACCNO);
				String name = rs.getString("name");
				String mobile = rs.getString(MOBILE);
				String purpose = rs.getString(PURPOSE);
				String status = rs.getString(STATUS);
				Customer s = new Customer();
				s.setAccountNumber(acno);
				s.setName(name);
				s.setMobile(mobile);
				s.setPurpose(purpose);
				s.setStatus(status);
				al3.add(s);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return al3;
	}
	
	public String fetchStatusByAccountNo(String accountNo) throws SQLException  {
	    String status = null;
	    Connection con =db. getConnection();
	    
		try(PreparedStatement ps = con.prepareStatement("SELECT status FROM loan_application WHERE account_number=?");) {
			
			ps.setString(1, accountNo);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				status = rs.getString(STATUS);
				
			}
		} 
	   
	    return status;
	}

	public String fetchLoanAccountNo(String accountNo) {
	    String loanAcNo = null;
	    Connection con =db. getConnection();
	    try(PreparedStatement ps = con.prepareStatement("SELECT loan_AccountNo FROM loan_application WHERE account_number=?");) {
	    	ps.setString(1, accountNo);
	    	ResultSet rs = ps.executeQuery();
	    	if (rs.next()) {
	    		loanAcNo = rs.getString("loan_AccountNo");
	    	}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	   
	    return loanAcNo;
	}
	
	public List<Customer> fetchByPurpose(String purpose) {
	    List<Customer> customers = new ArrayList<>();
	    try (Connection conn = db.getConnection();
	    		 FilteredRowSet frs = RowSetProvider.newFactory().createFilteredRowSet();) {
	       

	        frs.setCommand("SELECT account_number, name, purpose, created_at, loan_AccountNo, mobile, loan_amount, emi, status FROM loan_application");
	        frs.execute(conn);

	        frs.setFilter(new PurposeFilter(purpose));

	        while (frs.next()) {
	            Customer c = new Customer();
	            c.setAccountNumber(frs.getString(ACCNO));
	            c.setName(frs.getString("name"));
	            c.setPurpose(frs.getString(PURPOSE));
	            c.setCreatedAt(frs.getTimestamp(CREATED));
	            c.setAccountNo(frs.getString("loan_AccountNo"));
	            c.setMobile(frs.getString(MOBILE));
	            c.setLoanAmount(frs.getBigDecimal("loan_amount"));
	            c.setEmi(frs.getDouble("emi"));
	            c.setStatus(frs.getString(STATUS));

	            customers.add(c);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return customers;
	}

}
