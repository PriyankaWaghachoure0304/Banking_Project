package com.sdp.model;

import java.util.Date;

public class TransferPojo {
	
	public TransferPojo(String fromAccount, String toAccount, Double amount, String transferType, String remark,
			String from_direction, Date dateTime) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
		this.transferType = transferType;
		this.remark = remark;
		this.from_direction = from_direction;
		this.dateTime = dateTime;
	}
	
	public TransferPojo() {
		super();
	}

	String fromAccount;
	String toAccount;
	Double amount;
	String transferType;
	Double charges;
	String remark;
	String from_direction;
	Date dateTime;
	
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getFrom_direction() {
		return from_direction;
	}
	public void setFrom_direction(String from_direction) {
		this.from_direction = from_direction;
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
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public Double getCharges() {
		return charges;
	}
	public void setCharges(Double charges) {
		this.charges = charges;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "TransferPojo [fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", amount=" + amount
				+ ", transferType=" + transferType + ", charges=" + charges + ", remark=" + remark + "]";
	}
	
	

}
