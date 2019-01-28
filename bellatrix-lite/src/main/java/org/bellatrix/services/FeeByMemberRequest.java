package org.bellatrix.services;

public class FeeByMemberRequest {

	private Integer id;
	private Integer feeID;
	private String username;
	private boolean destination;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFeeID() {
		return feeID;
	}

	public void setFeeID(Integer feeID) {
		this.feeID = feeID;
	}

	public boolean isDestination() {
		return destination;
	}

	public void setDestination(boolean destination) {
		this.destination = destination;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
