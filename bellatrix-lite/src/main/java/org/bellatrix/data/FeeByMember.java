package org.bellatrix.data;

public class FeeByMember {

	private Integer id;
	private Integer feeID;
	private Integer memberID;
	private String name;
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

	public Integer getMemberID() {
		return memberID;
	}

	public void setMemberID(Integer memberID) {
		this.memberID = memberID;
	}

	public boolean isDestination() {
		return destination;
	}

	public void setDestination(boolean destination) {
		this.destination = destination;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
