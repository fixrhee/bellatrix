package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ResponseStatus;

public class LoadWelcomeMenuResponse {
	
	private List<String> welcomeLink;
	private ResponseStatus status;

	public List<String> getWelcomeLink() {
		return welcomeLink;
	}

	public void setWelcomeLink(List<String> welcomeLink) {
		this.welcomeLink = welcomeLink;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
