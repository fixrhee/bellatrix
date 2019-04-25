package org.bellatrix.services;

public class LoadBrokerRequest {

	private String fromMember;
	private String toMember;
	private Integer feeID;
	private Integer id;

	public String getFromMember() {
		return fromMember;
	}

	public void setFromMember(String fromMember) {
		this.fromMember = fromMember;
	}

	public String getToMember() {
		return toMember;
	}

	public void setToMember(String toMember) {
		this.toMember = toMember;
	}

	public Integer getFeeID() {
		return feeID;
	}

	public void setFeeID(Integer feeID) {
		this.feeID = feeID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
