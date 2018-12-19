package org.bellatrix.services;

import org.bellatrix.data.ResponseStatus;

public class UnreadMessage {

	private Integer unread;
	private ResponseStatus status;

	public Integer getUnread() {
		return unread;
	}

	public void setUnread(Integer unread) {
		this.unread = unread;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
