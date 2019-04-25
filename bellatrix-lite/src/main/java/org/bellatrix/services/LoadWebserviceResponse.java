package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.ResponseStatus;
import org.bellatrix.data.WebServices;

public class LoadWebserviceResponse {

	private List<WebServices> webservice;
	private Integer totalRecords;
	private ResponseStatus status;

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public List<WebServices> getWebservice() {
		return webservice;
	}

	public void setWebservice(List<WebServices> webservice) {
		this.webservice = webservice;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

}
