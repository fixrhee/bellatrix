package org.bellatrix.services;

public class WebservicePermissionRequest {

	private Integer id;
	private Integer webserviceID;
	private Integer groupID;

	public Integer getWebserviceID() {
		return webserviceID;
	}

	public void setWebserviceID(Integer webserviceID) {
		this.webserviceID = webserviceID;
	}

	public Integer getGroupID() {
		return groupID;
	}

	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
