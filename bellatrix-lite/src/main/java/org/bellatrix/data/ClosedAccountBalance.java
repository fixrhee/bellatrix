package org.bellatrix.data;

import java.math.BigDecimal;

public class ClosedAccountBalance {

	private Integer memberID;
	private Integer accountID;
	private Integer lastTransferID;
	private BigDecimal balance;

	public Integer getMemberID() {
		return memberID;
	}

	public void setMemberID(Integer memberID) {
		this.memberID = memberID;
	}

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer accountID) {
		this.accountID = accountID;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Integer getLastTransferID() {
		return lastTransferID;
	}

	public void setLastTransferID(Integer lastTransferID) {
		this.lastTransferID = lastTransferID;
	}

}
