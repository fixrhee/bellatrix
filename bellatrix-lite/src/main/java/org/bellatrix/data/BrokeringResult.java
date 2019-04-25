package org.bellatrix.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BrokeringResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3459006939283799838L;
	private BigDecimal feeAmount;
	private BigDecimal totalBrokeringAmount;
	private List<Brokers> listBrokers;

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public BigDecimal getTotalBrokeringAmount() {
		return totalBrokeringAmount;
	}

	public void setTotalBrokeringAmount(BigDecimal totalBrokeringAmount) {
		this.totalBrokeringAmount = totalBrokeringAmount;
	}

	@Override
	public String toString() {
		return "[FeeAmount : " + feeAmount + ", TotalBrokeringAmount : " + totalBrokeringAmount + "]";
	}

	public List<Brokers> getListBrokers() {
		return listBrokers;
	}

	public void setListBrokers(List<Brokers> listBrokers) {
		this.listBrokers = listBrokers;
	}

}
