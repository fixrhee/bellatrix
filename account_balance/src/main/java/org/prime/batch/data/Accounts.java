package org.prime.batch.data;

import java.math.BigDecimal;

public class Accounts {

	private Integer id;
	private Integer currencyID;
	private String accountName;
	private boolean systemAccount;
	private BigDecimal balance;
	private BigDecimal reservedAmount;
	private Integer lastTransferID;
	private Members member;
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(Integer currencyID) {
		this.currencyID = currencyID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Members getMember() {
		return member;
	}

	public void setMember(Members member) {
		this.member = member;
	}

	public boolean isSystemAccount() {
		return systemAccount;
	}

	public void setSystemAccount(boolean systemAccount) {
		this.systemAccount = systemAccount;
	}

	public Integer getLastTransferID() {
		return lastTransferID;
	}

	public void setLastTransferID(Integer lastTransferID) {
		this.lastTransferID = lastTransferID;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getReservedAmount() {
		return reservedAmount;
	}

	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}
}
