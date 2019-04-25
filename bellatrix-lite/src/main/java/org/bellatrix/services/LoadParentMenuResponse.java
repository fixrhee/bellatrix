package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ParentMenu;
import org.bellatrix.data.ParentMenuData;
import org.bellatrix.data.ResponseStatus;

public class LoadParentMenuResponse {

	private List<ParentMenuData> parentMenu;
	private ResponseStatus status;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public List<ParentMenuData> getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(List<ParentMenuData> parentMenu) {
		this.parentMenu = parentMenu;
	}
}
