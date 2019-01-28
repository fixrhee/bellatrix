package org.bellatrix.services;

public class NotificationRequest {

	private Integer id;
	private String name;
	private String moduleURL;
	private Integer currentPage;
	private Integer pageSize;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModuleURL() {
		return moduleURL;
	}

	public void setModuleURL(String moduleURL) {
		this.moduleURL = moduleURL;
	}

}
