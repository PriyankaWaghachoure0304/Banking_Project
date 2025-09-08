package com.sdp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.core.Logger;
import org.zkoss.zul.Messagebox;

import com.sdp.apachelog4j.LoggerExample;
import com.sdp.connection.DbConnection;
import com.sdp.model.AddressDocuments;
import com.sdp.model.PersonalDetails;

import co.sdp.lazyloading.LongOprations;

public class AccountDAO {
	
	DbConnection db = new DbConnection();
	
	Logger logger = LoggerExample.getLogger();
	
	private static final String INSERT_PERSONALDETAIL_SQUERY = "INSERT INTO personal_details (account_type, credit_card, first_name, middle_name, last_name, dob,"
    		+ " nationality, guardian_name, gender, city_of_birth, annual_income, id_proof, occupation, id_number,"
    		+ " country_code, mobile_number, email, account_no)"
    		+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public boolean insert(PersonalDetails pd, AddressDocuments doc) {
		logger.debug("Enter in insert()");
		
			Connection con1 = db.getConnection();
				Connection con2 = db.getConnection();
				Connection con3 = db.getConnection();
				Connection con4 = db.getConnection();
				Connection con5 = db.getConnection();
				try{
			
					con1.setAutoCommit(false);
					con2.setAutoCommit(false);
					con3.setAutoCommit(false);
					con4.setAutoCommit(false);
					con5.setAutoCommit(false);
					
					 if(!isUniqueEmail(pd.getEmail())) {
			            	Messagebox.show("User Already exist!");
			            	logger.debug("Exit from insert()");
			            	return false;
			            }
			
				if(insertPersonalDetails(pd, con1)) {
					con1.commit();
				}
				else {
					con1.rollback();
					logger.debug("Exit from insert()");
					return false;
				}
				
	
				if(insertAddressDocuments(doc, con2)) {
					con2.commit();
				}
				else {
					con1.rollback();
					con2.rollback();
					logger.debug("Exit from insert()");
					return false;
				}
				
				
				
				String accno = pd.getAccountNo();
				
				 int cusid = selectAccountId(accno);
				 if(cusid==0) {
					 con1.rollback();
					 con2.rollback();
					 logger.debug("Exit from insert()");
					 return false;
				 }
				
				String fullname= pd.getFirstName()+" "+pd.getMiddleName()+" "+pd.getLastName();
	            String email=pd.getEmail();
	            String phone =  pd.getMobileNumber().toString();
				
				if(insertUserInfomatin(fullname, email, phone,cusid,accno, con3)) {
					con3.commit();
				}
				else {
					con1.rollback();
					con2.rollback();
					con3.rollback();
					logger.debug("Exit from insert()");
					return false;
				}
				
				if(insertdebitcard(accno, generateDebitCardNumber(), fullname, con4)) {
					con4.commit();
				}
				else {
					con1.rollback();
					con2.rollback();
					con3.rollback();
					con4.rollback();
					logger.debug("Exit from insert()");
					return false;
				}
				
				if(insertAccountBalance(accno,con5)) {
					con5.commit();
				}
				else {
					con1.rollback();
					con2.rollback();
					con3.rollback();
					con4.rollback();
					con5.rollback();
					logger.debug("Exit from insert()");
					return false;
				}
				
				
				
				
				con1.close();
				con2.close();
				con3.close();
				con4.close();
				con5.close();
				logger.debug("Exit from insert()");
				return true;
			
		}
		catch (Exception e) {
			try {
				con1.rollback();
				con2.rollback();
				con3.rollback();
				con4.rollback();
				con5.rollback();
				return false;
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error("Error in insert details"+e);
			}
			e.printStackTrace();
			
		}
				return true;
	}
	
	
	
	
	

    public boolean insertPersonalDetails(PersonalDetails pd, Connection con) throws SQLException  {
    	logger.debug("Enter in PersonalDetail()");
       try( PreparedStatement ps = con.prepareStatement(INSERT_PERSONALDETAIL_SQUERY);){
        	con.setAutoCommit(false);
            ps.setString(1, pd.getAccountType());
            ps.setBoolean(2, pd.isCreditCard());
            ps.setString(3, pd.getFirstName());
            ps.setString(4, pd.getMiddleName());
            ps.setString(5, pd.getLastName());
            ps.setDate(6, new java.sql.Date(pd.getDob().getTime()));
            ps.setString(7, pd.getNationality());
            ps.setString(8, pd.getGuardianName());
            ps.setString(9, pd.getGender());
            ps.setString(10, pd.getCityOfBirth());
            ps.setBigDecimal(11, pd.getAnnualIncome());
            ps.setString(12, pd.getIdProof());
            ps.setString(13, pd.getOccupation());
            ps.setString(14, pd.getIdNumber());
            ps.setString(15, pd.getCountryCode());
            ps.setLong(16, pd.getMobileNumber());
            ps.setString(17, pd.getEmail());
            ps.setString(18, pd.getAccountNo());
            
   
            logger.debug("Exit from PersonalDetail()");
            return ps.executeUpdate() > 0;
       
       }
    }
    
    private static final String INSERTQUERY = "INSERT INTO accounts (account_number) values(?)";
    public boolean insertAccountBalance(String accountNum, Connection con) throws SQLException {
    	logger.debug("Enter in accountBalance()");
    	try( PreparedStatement ps2 = con.prepareStatement(INSERTQUERY) ;){
    		 ps2.setString(1, accountNum);
    		 logger.debug("Exit from accountBalance()");
				return ps2.executeUpdate()>0;
			
    	}
    	
    }
    
    
    
    private static final String USERSINSERTQUERY = "INSERT INTO users (full_name,email,phone_number,account_number,customer_id) values(?,?,?,?,?)";
    public boolean insertUserInfomatin(String fullname,String email,String phonenumber,int customerid,String accountNum, Connection con) throws SQLException{
    	logger.debug("Enter in userInfo()");
				try( PreparedStatement ps4 = con.prepareStatement(USERSINSERTQUERY);){
    		 ps4.setString(1, fullname);
    		 ps4.setString(2, email);
    		 ps4.setString(3, phonenumber);
    		 ps4.setInt(5, customerid);
    		 ps4.setString(4, accountNum);
    		 
    		 logger.debug("Exit from userInfo()");
    		 return ps4.executeUpdate()>0;
				}
		 
    }
    
    

    public boolean insertAddressDocuments(AddressDocuments doc, Connection con) throws SQLException  {
        String sql = "INSERT INTO address_and_documents (current_address, current_city, current_district,"
        		+ " current_state, current_pin, current_country, permanent_address, permanent_city, "
        		+ "permanent_district, permanent_state, permanent_pin, permanent_country, photo_path, aadhar_path,"
        		+ " pan_path, account_no) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        logger.debug("Enter in insertDocument()");
        
        try(PreparedStatement ps = con.prepareStatement(sql);){
            ps.setString(1, doc.getCurrentAddress());
            ps.setString(2, doc.getCurrentCity());
            ps.setString(3, doc.getCurrentDistrict());
            ps.setString(4, doc.getCurrentState());
            ps.setString(5, doc.getCurrentPin());
            ps.setString(6, doc.getCurrentCountry());
            ps.setString(7, doc.getPermanentAddress());
            ps.setString(8, doc.getPermanentCity());
            ps.setString(9, doc.getPermanentDistrict());
            ps.setString(10, doc.getPermanentState());
            ps.setString(11, doc.getPermanentPin());
            ps.setString(12, doc.getPermanentCountry());
            ps.setBytes(13, doc.getPhotoBytes());
            ps.setBytes(14, doc.getAadharBytes());
            ps.setBytes(15, doc.getPanBytes());
            ps.setString(16, doc.getAccountNo());
            
            logger.debug("Exit from insertDocument()");
            
            return ps.executeUpdate()>0;
        }
        
    }
    
    
    public boolean isUniqueEmail(String email) throws SQLException  {
        try (Connection con = db.getConnection();
        		PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(1) as e FROM users where email=?");
        		) {
        	ps1.setString(1, email);
           
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            return rs1.getInt("e") < 1;
               
        }
    }

    public String generateAccountNumber() throws SQLException {
    	logger.debug("Enter in generateAccountNumber()");
        try (Connection con = db.getConnection();
        		PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(1) as ac FROM personal_details");
        		PreparedStatement ps = con.prepareStatement("SELECT MAX(account_no) as acc FROM personal_details");) {
           
            ResultSet rs1 = ps1.executeQuery();
            rs1.next();
            if (rs1.getInt("ac") < 1) {
                return "258789461000";
            }
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            logger.info("Account number is generated");
            return String.valueOf(rs.getLong("acc") + 1);
        }
    }
    
    
    
    private static final String PERSONALSELECTQUERY = "SELECT email,customer_id from personal_details where account_no=?";
    public int selectAccountId(String accountNum) {
    	try (Connection con = db.getConnection();
				 PreparedStatement ps5 = con.prepareStatement(PERSONALSELECTQUERY) ;){
    		 ps5.setString(1, accountNum);
    		 ResultSet rs5 = ps5.executeQuery();
    		if(rs5.next())
    		 {
    			int customerid = rs5.getInt("customer_id");
    			String mail = rs5.getString("email");
				Messagebox.show("Your Customer Id Is : "+customerid + "\n"+"And Account Number Is : "+accountNum);
				 LongOprations.submitTask(()->
				 {
					try {
						sendEmail(mail, customerid, accountNum);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				});
				
				return customerid;
			}
		 }catch (Exception e) {
			 e.printStackTrace();
		}
    	return 0;
    }
    
public void sendEmail(String toEmail, int customerid, String accno) throws MessagingException {
	    String host = "smtp.gmail.com"; 
	    String from = "sanvika54321@gmail.com"; 
	    String password = "lrpk bqms wpyl mard"; 

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", "587");

	    Session session = Session.getInstance(props, new Authenticator() {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(from, password);
	        }
	    });

	    String subject = "Welcome to SDP Horizon Bank – Your Account Has Been Successfully Created";

	    String emailBody = "Dear Customer,\n\n"
	            + "Thank you for choosing SDP Horizon Bank.\n\n"
	            + "We are pleased to inform you that your new account has been successfully created.\n\n"
	            + "Here are your account details:\n"
	            + "Customer ID      : " + customerid + "\n"
	            + "Account Number   : " + accno + "\n\n"
	            + "Please keep this information safe and do not share it with anyone.\n\n"
	            + "If you have any questions or need further assistance, feel free to contact our customer support team.\n\n"
	            + "Warm regards,\n"
	            + "SDP Horizon Bank\n"
	            + "Customer Service Team";

	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(from));
	    message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
	    message.setSubject(subject);
	    message.setText(emailBody);

	    Transport.send(message);
	    logger.info("Email send successfully!!!");
	}

public String generateDebitCardNumber() throws SQLException  {
	logger.debug("Enter in generateDebitCardNumber()");
    try (Connection con = db.getConnection();
    		PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(1) as ac FROM debit_card");
    		PreparedStatement ps = con.prepareStatement("SELECT MAX(card_number) as acc FROM debit_card");) {
       
        ResultSet rs1 = ps1.executeQuery();
        rs1.next();
        if (rs1.getInt("ac") < 1) {
            return "2587894610001234";
        }
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        logger.debug("Exit from generateDebitCardNumber()");
        return String.valueOf(rs.getLong("acc") + 1);
    }
}

private static final String DEBITINSERTQUERY = "INSERT INTO debit_card (account_no,card_number,card_holder_name) values(?,?,?)";
public boolean insertdebitcard(String accno,String cardnumber,String cardholdername, Connection con) throws SQLException  {
	try(PreparedStatement ps4 = con.prepareStatement(DEBITINSERTQUERY);){
		 ps4.setString(1, accno);
		 ps4.setString(2, cardnumber);
		 ps4.setString(3, cardholdername);
		 
		 
		return ps4.executeUpdate()>0;
	}
	 
}

    
}