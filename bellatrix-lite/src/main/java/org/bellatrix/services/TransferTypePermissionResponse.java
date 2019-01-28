package org.bellatrix.services;

import java.util.List;
import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.TransferTypePermission;

public class TransferTypePermissionResponse {

	private ResponseStatus status;
	private List<TransferTypePermission> permission;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public List<TransferTypePermission> getPermission() {
		return permission;
	}

	public void setPermission(List<TransferTypePermission> permission) {
		this.permission = permission;
	}

}
