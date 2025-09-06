package com.sdp.model;

import java.time.LocalDateTime;

public class Receipt {
	
	String transactionId;
	String fromAccount;
	String toAccount;
	Double amount;
	String transactionType;
	Double tax;
	String remark;
	Double availableBalance;
	LocalDateTime time;
	String fromDirection;
	
	
	
	public String getFromDirection() {
		return fromDirection;
	}
	public void setFromDirection(String fromDirection) {
		this.fromDirection = fromDirection;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String tId) {
		this.transactionId = tId;
	}
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public Double getTax() {
		return tax;
	}
	public void setTax(Double tax) {
		this.tax = tax;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	@Override
	public String toString() {
		return "Receipt [transactionId=" + transactionId + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount
				+ ", amount=" + amount + ", transactionType=" + transactionType + ", tax=" + tax + ", remark=" + remark
				+ ", availableBalance=" + availableBalance + ", time=" + time + "]";
	}
	
	
	

}
