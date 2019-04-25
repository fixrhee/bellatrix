package org.bellatrix.services;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import org.bellatrix.data.Members;
import org.bellatrix.data.PaymentFields;
import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.TransferTypeFields;

public class PaymentResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4287862721338934384L;
	private Integer id;
	private Members fromMember;
	private Members toMember;
	private String description;
	private BigDecimal amount;
	private BigDecimal finalAmount;
	private BigDecimal totalFees;
	private String transactionNumber;
	private String traceNumber;
	private List<PaymentFields> paymentFields;
	private TransferTypeFields transferType;
	private ResponseStatus status;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getTraceNumber() {
		return traceNumber;
	}

	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}

	public List<PaymentFields> getPaymentFields() {
		return paymentFields;
	}

	public void setPaymentFields(List<PaymentFields> paymentFields) {
		this.paymentFields = paymentFields;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(BigDecimal totalFees) {
		this.totalFees = totalFees;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Members getToMember() {
		return toMember;
	}

	public void setToMember(Members toMember) {
		this.toMember = toMember;
	}

	public Members getFromMember() {
		return fromMember;
	}

	public void setFromMember(Members fromMember) {
		this.fromMember = fromMember;
	}

	public TransferTypeFields getTransferType() {
		return transferType;
	}

	public void setTransferType(TransferTypeFields transferType) {
		this.transferType = transferType;
	}

}
