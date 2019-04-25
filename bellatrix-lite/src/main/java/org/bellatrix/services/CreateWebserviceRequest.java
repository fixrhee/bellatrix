package org.bellatrix.services;

public class CreateWebserviceRequest {

	private Integer id;
	private String username;
	private String name;
	private String password;
	private String hash;
	private boolean active;
	private boolean secureTransaction;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSecureTransaction() {
		return secureTransaction;
	}

	public void setSecureTransaction(boolean secureTransaction) {
		this.secureTransaction = secureTransaction;
	}

}
