package com.sdp.customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
/**
 * Represents a customer and their loan application details.
 * Implements Serializable for session and persistence support.
 */
public class Customer implements Serializable{
	private static final long serialVersionUID = 1L;
	private int applicationId;
	private String name;
	private Date dob;
	private String gender;
	private String maritalStatus;
	private String mobile;
	private String email;
	private String occupation;
	private String employerName;
	private BigDecimal grossIncome;
	private BigDecimal netIncome;
	private BigDecimal loanAmount;
	private Integer tenureMonths;
	private String purpose;
	private String repaymentMode;
	private String propertyType;
	private BigDecimal propertyValue;
	private String propertyAddress;
	private Boolean needLifeCover;
	private Boolean needSurakshaLoan;
	private Boolean agreedTerms;
	private Timestamp createdAt;
	private String accountNumber;
	
	
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	private Double emi;
	private String accountNo;
	private String status;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	


	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public Double getEmi() {
		return emi;
	}

	public void setEmi(Double emi) {
		this.emi = emi;
	}

	public int getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(int applicationId) {
		this.applicationId = applicationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getEmployerName() {
		return employerName;
	}

	public void setEmployerName(String employerName) {
		this.employerName = employerName;
	}

	

	public Integer getTenureMonths() {
		return tenureMonths;
	}

	public void setTenureMonths(Integer tenureMonths) {
		this.tenureMonths = tenureMonths;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getRepaymentMode() {
		return repaymentMode;
	}

	public void setRepaymentMode(String repaymentMode) {
		this.repaymentMode = repaymentMode;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	

	public BigDecimal getGrossIncome() {
		return grossIncome;
	}

	public void setGrossIncome(BigDecimal grossIncome) {
		this.grossIncome = grossIncome;
	}

	public BigDecimal getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(BigDecimal netIncome) {
		this.netIncome = netIncome;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public BigDecimal getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(BigDecimal propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public Boolean getNeedLifeCover() {
		return needLifeCover;
	}

	public void setNeedLifeCover(Boolean needLifeCover) {
		this.needLifeCover = needLifeCover;
	}

	public Boolean getNeedSurakshaLoan() {
		return needSurakshaLoan;
	}

	public void setNeedSurakshaLoan(Boolean needSurakshaLoan) {
		this.needSurakshaLoan = needSurakshaLoan;
	}

	public Boolean getAgreedTerms() {
		return agreedTerms;
	}

	public void setAgreedTerms(Boolean agreedTerms) {
		this.agreedTerms = agreedTerms;
	}

	@Override
	public String toString() {
		return "Customer [applicationId=" + applicationId + ", name=" + name + ", dob=" + dob + ", gender=" + gender
				+ ", maritalStatus=" + maritalStatus + ", mobile=" + mobile + ", email=" + email + ", occupation="
				+ occupation + ", employerName=" + employerName + ", grossIncome=" + grossIncome + ", netIncome="
				+ netIncome + ", loanAmount=" + loanAmount + ", tenureMonths=" + tenureMonths + ", purpose=" + purpose
				+ ", repaymentMode=" + repaymentMode + ", propertyType=" + propertyType + ", propertyValue="
				+ propertyValue + ", propertyAddress=" + propertyAddress + ", needLifeCover=" + needLifeCover
				+ ", needSurakshaLoan=" + needSurakshaLoan + ", agreedTerms=" + agreedTerms + ", createdAt=" + createdAt
				+ ", accountNumber=" + accountNumber + ", emi=" + emi + ", accountNo=" + accountNo + ", status="
				+ status + "]";
	}

	

	

}
