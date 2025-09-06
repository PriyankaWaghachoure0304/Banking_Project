package com.sdp.model;

import java.io.Serializable;

public class Notification implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String accountNumber;
	private String message;
	private Boolean isSeen;
	private String dateTime;
	
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getIsSeen() {
		return isSeen;
	}
	public void setIsSeen(Boolean isSeen) {
		this.isSeen = isSeen;
	}
	
	
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	@Override
	public String toString() {
		return "Notification [accountNumber=" + accountNumber + ", message=" + message + ", isSeen=" + isSeen
				+ ", dateTime=" + dateTime + "]";
	}

	
	
	
	

}
