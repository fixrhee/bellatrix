package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ResponseStatus;

public class PendingPaymentResponse {

	private List<PaymentResponse> payment;
	private ResponseStatus status;

	public List<PaymentResponse> getPayment() {
		return payment;
	}

	public void setPayment(List<PaymentResponse> payment) {
		this.payment = payment;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
