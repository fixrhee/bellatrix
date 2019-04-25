package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ChildMenu;
import org.bellatrix.data.ResponseStatus;

public class LoadChildMenuResponse {
	
	private List<ChildMenu> childMenu;
	private ResponseStatus status;

	public List<ChildMenu> getChildMenu() {
		return childMenu;
	}

	public void setChildMenu(List<ChildMenu> childMenu) {
		this.childMenu = childMenu;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
