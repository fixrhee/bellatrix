package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.TransferNotifications;

public class TransferTypeNotificationResponse {

	private List<TransferNotifications> notification;
	private ResponseStatus status;

	public List<TransferNotifications> getNotification() {
		return notification;
	}

	public void setNotification(List<TransferNotifications> notification) {
		this.notification = notification;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
