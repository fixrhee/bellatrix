package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.AccessType;
import org.bellatrix.data.ResponseStatus;

public class LoadAccessTypeResponse {

	private List<AccessType> accessType;
	private ResponseStatus status;

	public List<AccessType> getAccessType() {
		return accessType;
	}

	public void setAccessType(List<AccessType> accessType) {
		this.accessType = accessType;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
