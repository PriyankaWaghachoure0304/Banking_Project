package com.sdp.model;

import java.util.Date;

public class CreditCard_apply {

	private String accountNumber;
    private String fullName;
    private Date dateOfBirth;
    private String mobileNumber;
    private String email;
    private String address;
    private String idProof;  
    private Double requestedLimit;
    private Date createdAt;
    private String cardNumber; 
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIdProof() {
		return idProof;
	}
	public void setIdProof(String idProof) {
		this.idProof = idProof;
	}
	public Double getRequestedLimit() {
		return requestedLimit;
	}
	public void setRequestedLimit(Double requestedLimit) {
		this.requestedLimit = requestedLimit;
	}
	@Override
	public String toString() {
		return "CreditCard_apply [accountNumber=" + accountNumber + ", fullName=" + fullName + ", dateOfBirth="
				+ dateOfBirth + ", mobileNumber=" + mobileNumber + ", email=" + email + ", address=" + address
				+ ", idProof=" + idProof + ", requestedLimit=" + requestedLimit + ", createdAt=" + createdAt
				+ ", cardNumber=" + cardNumber + "]";
	}
    
    
}
