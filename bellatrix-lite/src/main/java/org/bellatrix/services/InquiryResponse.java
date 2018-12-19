package org.bellatrix.services;

import java.io.Serializable;
import java.math.BigDecimal;
import org.bellatrix.data.Members;
import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.TransferTypeFields;

public class InquiryResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8116540986300848351L;
	private Members fromMember;
	private Members toMember;
	private BigDecimal transactionAmount;
	private BigDecimal finalAmount;
	private BigDecimal totalFees;
	private TransferTypeFields transferType;
	private ResponseStatus status;

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

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}

	public BigDecimal getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(BigDecimal totalFees) {
		this.totalFees = totalFees;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public TransferTypeFields getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferTypeFields transferType) {
		this.transferType = transferType;
	}
}
