package org.bellatrix.services;

import java.io.Serializable;

public class RequestEviction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7453023740897412546L;
	private Long requestEvictionSeconds;
	private boolean deleteRequestOnEviction;

	public Long getRequestEvictionSeconds() {
		return requestEvictionSeconds;
	}

	public void setRequestEvictionSeconds(Long requestEvictionSeconds) {
		this.requestEvictionSeconds = requestEvictionSeconds;
	}

	public boolean isDeleteRequestOnEviction() {
		return deleteRequestOnEviction;
	}

	public void setDeleteRequestOnEviction(boolean deleteRequestOnEviction) {
		this.deleteRequestOnEviction = deleteRequestOnEviction;
	}

}
