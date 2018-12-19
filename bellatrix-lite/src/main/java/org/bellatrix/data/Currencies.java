package org.bellatrix.data;

import java.io.Serializable;

public class Currencies implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7922429158927544627L;
	private Integer id;
	private String name;
	private String code;
	private String prefix;
	private String trailer;
	private String format;
	private char grouping;
	private char decimal;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public char getGrouping() {
		return grouping;
	}

	public void setGrouping(char grouping) {
		this.grouping = grouping;
	}

	public char getDecimal() {
		return decimal;
	}

	public void setDecimal(char decimal) {
		this.decimal = decimal;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

}
