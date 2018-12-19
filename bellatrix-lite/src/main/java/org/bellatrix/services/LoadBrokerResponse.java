package org.bellatrix.services;

import java.util.List;

import org.bellatrix.data.Brokers;
import org.bellatrix.data.ResponseStatus;

public class LoadBrokerResponse {

	private List<Brokers> brokers;
	private ResponseStatus status;

	public List<Brokers> getBrokers() {
		return brokers;
	}

	public void setBrokers(List<Brokers> brokers) {
		this.brokers = brokers;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
}
