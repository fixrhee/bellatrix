package org.bellatrix.services;

import org.bellatrix.data.Fees;
import org.bellatrix.data.ResponseStatus;

public class LoadFeesByIDResponse {

	private Fees fee;
	private ResponseStatus status;

	public Fees getFee() {
		return fee;
	}

	public void setFee(Fees fee) {
		this.fee = fee;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
