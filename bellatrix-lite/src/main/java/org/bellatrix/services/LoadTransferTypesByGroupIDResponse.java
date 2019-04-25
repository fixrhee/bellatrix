package org.bellatrix.services;

import java.util.List;
import org.bellatrix.data.ResponseStatus;

public class LoadTransferTypesByGroupIDResponse {

	private List<TransferType> transferTypes;
	private ResponseStatus status;

	public List<TransferType> getTransferTypes() {
		return transferTypes;
	}

	public void setTransferTypes(List<TransferType> transferTypes) {
		this.transferTypes = transferTypes;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
