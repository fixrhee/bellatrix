package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.WelcomeMenu;

public class LoadWelcomeMenuResponse {

	private List<WelcomeMenu> welcomeLink;
	private ResponseStatus status;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public List<WelcomeMenu> getWelcomeLink() {
		return welcomeLink;
	}

	public void setWelcomeLink(List<WelcomeMenu> welcomeLink) {
		this.welcomeLink = welcomeLink;
	}

}
