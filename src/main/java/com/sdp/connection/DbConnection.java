package com.sdp.connection;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbConnection implements Serializable{

	private static final long serialVersionUID = 1L;

	public Connection getConnection() {
		
		 Properties prop = new Properties();
		 Connection con=null;
	     
		 try(InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");) {
	         prop.load(input);

	         String dbUrl = prop.getProperty("db.url");
	         String username = prop.getProperty("db.username");
	         String password = prop.getProperty("db.password");
	         String dbClass = prop.getProperty("db.class");

	         Class.forName(dbClass);
	         
	        con = DriverManager.getConnection(dbUrl, username, password);
	            
	         
	     } catch (Exception e) {
	         e.printStackTrace();
	     }

		 return con;

	}

}
