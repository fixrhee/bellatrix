package org.bellatrix.services;

import java.math.BigDecimal;
import org.bellatrix.data.Members;
import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.TransferTypeFields;
import org.bellatrix.data.TransferTypes;

public class RequestPaymentConfirmationResponse {

	private String requestID;
	private Members fromMember;
	private Members toMember;
	private BigDecimal transactionAmount;
	private BigDecimal finalAmount;
	private BigDecimal totalFees;
	private TransferTypeFields transferType;
	private boolean twoFactorAuthentication;

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

	private ResponseStatus status;

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
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

	public boolean isTwoFactorAuthentication() {
		return twoFactorAuthentication;
	}

	public void setTwoFactorAuthentication(boolean twoFactorAuthentication) {
		this.twoFactorAuthentication = twoFactorAuthentication;
	}

	public TransferTypeFields getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferTypeFields transferType) {
		this.transferType = transferType;
	}

}
