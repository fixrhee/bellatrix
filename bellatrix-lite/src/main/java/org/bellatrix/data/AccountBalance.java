package org.bellatrix.data;

import java.math.BigDecimal;

public class AccountBalance {

	private BigDecimal balance = BigDecimal.ZERO;
	private BigDecimal reservedBalance = BigDecimal.ZERO;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal creditLimit = BigDecimal.ZERO;
	private boolean source;

	public AccountBalance() {
	}

	public AccountBalance(BigDecimal balance, BigDecimal reservedBalance, BigDecimal amount, boolean source) {
		this.balance = balance;
		this.reservedBalance = reservedBalance;
		this.source = source;
		this.amount = amount;
	}

	public AccountBalance(BigDecimal balance, BigDecimal reservedBalance, BigDecimal amount, BigDecimal creditLimit,
			boolean source) {
		this.balance = balance;
		this.reservedBalance = reservedBalance;
		this.source = source;
		this.amount = amount;
		this.creditLimit = creditLimit;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getReservedBalance() {
		return reservedBalance;
	}

	public void setReservedBalance(BigDecimal reservedBalance) {
		this.reservedBalance = reservedBalance;
	}

	public BigDecimal getLastBalance() {
		if (source) {
			return getBalance().subtract(amount);
		} else {
			return getBalance().add(amount);
		}
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
}
