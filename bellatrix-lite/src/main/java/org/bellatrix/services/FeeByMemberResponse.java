package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.FeeByMember;
import org.bellatrix.data.ResponseStatus;

public class FeeByMemberResponse {

	private List<FeeByMember> feeByMember;
	private ResponseStatus status;

	public List<FeeByMember> getFeeByMember() {
		return feeByMember;
	}

	public void setFeeByMember(List<FeeByMember> feeByMember) {
		this.feeByMember = feeByMember;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
