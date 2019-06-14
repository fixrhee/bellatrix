package org.bellatrix.data;

import java.io.Serializable;
import java.math.BigDecimal;

public class Brokers implements Serializable {

	private static final long serialVersionUID = -2377898982801131818L;
	private Integer id;
	private Integer feeID;
	private String feeTransactionNumber;
	private Integer fromMemberID;
	private Members fromMember;
	private Integer toMemberID;
	private Members toMember;
	private Integer fromAccountID;
	private Integer toAccountID;
	private String name;
	private String description;
	private boolean enabled;
	private boolean deductAllFee;
	private BigDecimal fixedAmount;
	private BigDecimal percentageValue;
	private BigDecimal feeAmount;
	private AccountBalance fromLastBalance;
	private AccountBalance toLastBalance;
	private String transactionNumber;
	private String requestTransactionAmount;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFromMemberID() {
		return fromMemberID;
	}

	public void setFromMemberID(Integer fromMemberID) {
		this.fromMemberID = fromMemberID;
	}

	public Integer getToMemberID() {
		return toMemberID;
	}

	public void setToMemberID(Integer toMemberID) {
		this.toMemberID = toMemberID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public BigDecimal getFixedAmount() {
		return fixedAmount;
	}

	public void setFixedAmount(BigDecimal fixedAmount) {
		this.fixedAmount = fixedAmount;
	}

	public BigDecimal getPercentageValue() {
		return percentageValue;
	}

	public void setPercentageValue(BigDecimal percentageValue) {
		this.percentageValue = percentageValue;
	}

	public Integer getFromAccountID() {
		return fromAccountID;
	}

	public void setFromAccountID(Integer fromAccountID) {
		this.fromAccountID = fromAccountID;
	}

	public Integer getToAccountID() {
		return toAccountID;
	}

	public void setToAccountID(Integer toAccountID) {
		this.toAccountID = toAccountID;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public Members getFromMember() {
		return fromMember;
	}

	public void setFromMember(Members fromMember) {
		this.fromMember = fromMember;
	}

	public Members getToMember() {
		return toMember;
	}

	public void setToMember(Members toMember) {
		this.toMember = toMember;
	}

	public Integer getFeeID() {
		return feeID;
	}

	public void setFeeID(Integer feeID) {
		this.feeID = feeID;
	}

	public String getFeeTransactionNumber() {
		return feeTransactionNumber;
	}

	public void setFeeTransactionNumber(String feeTransactionNumber) {
		this.feeTransactionNumber = feeTransactionNumber;
	}

	public String getRequestTransactionAmount() {
		return requestTransactionAmount;
	}

	public void setRequestTransactionAmount(String requestTransactionAmount) {
		this.requestTransactionAmount = requestTransactionAmount;
	}

	public boolean isDeductAllFee() {
		return deductAllFee;
	}

	public void setDeductAllFee(boolean deductAllFee) {
		this.deductAllFee = deductAllFee;
	}

	public AccountBalance getFromLastBalance() {
		return fromLastBalance;
	}

	public void setFromLastBalance(AccountBalance fromLastBalance) {
		this.fromLastBalance = fromLastBalance;
	}

	public AccountBalance getToLastBalance() {
		return toLastBalance;
	}

	public void setToLastBalance(AccountBalance toLastBalance) {
		this.toLastBalance = toLastBalance;
	}

}
