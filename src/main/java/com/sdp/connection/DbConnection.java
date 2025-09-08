package com.sdp.connection;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.core.Logger;

import com.sdp.apachelog4j.LoggerExample;

public class DbConnection implements Serializable{

	private static final long serialVersionUID = 1L;
	Logger logger = LoggerExample.getLogger();
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
	         logger.fatal("❌ Cannot connect to database! Application will shut down.", e);
	     }

		 return con;

	}

}
