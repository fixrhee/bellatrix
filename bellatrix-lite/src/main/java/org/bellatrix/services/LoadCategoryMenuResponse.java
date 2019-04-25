package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.CategoryMenuData;
import org.bellatrix.data.ResponseStatus;

public class LoadCategoryMenuResponse {

	private List<CategoryMenuData> categoryMenu;
	private ResponseStatus status;

	public List<CategoryMenuData> getCategoryMenu() {
		return categoryMenu;
	}

	public void setCategoryMenu(List<CategoryMenuData> categoryMenu) {
		this.categoryMenu = categoryMenu;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

}
