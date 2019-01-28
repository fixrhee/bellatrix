package org.bellatrix.services;

public class LoadFeesByTransferTypeRequest {

	private Integer id;
	private boolean showAllStatus;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isShowAllStatus() {
		return showAllStatus;
	}

	public void setShowAllStatus(boolean showAllStatus) {
		this.showAllStatus = showAllStatus;
	}

}
