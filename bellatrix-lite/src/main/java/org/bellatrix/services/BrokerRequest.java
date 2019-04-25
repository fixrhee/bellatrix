package org.bellatrix.services;

import java.math.BigDecimal;

public class BrokerRequest {

	private Integer id;
	private Integer feeID;
	private String fromMember;
	private String toMember;
	private Integer fromAccountID;
	private Integer toAccountID;
	private String name;
	private String description;
	private boolean enabled;
	private boolean deductAllFee;
	private BigDecimal fixedAmount;
	private BigDecimal percentage;

	public Integer getFeeID() {
		return feeID;
	}

	public void setFeeID(Integer feeID) {
		this.feeID = feeID;
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

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getToMember() {
		return toMember;
	}

	public void setToMember(String toMember) {
		this.toMember = toMember;
	}

	public String getFromMember() {
		return fromMember;
	}

	public void setFromMember(String fromMember) {
		this.fromMember = fromMember;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isDeductAllFee() {
		return deductAllFee;
	}

	public void setDeductAllFee(boolean deductAllFee) {
		this.deductAllFee = deductAllFee;
	}

}
