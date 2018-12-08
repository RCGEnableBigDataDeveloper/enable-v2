package com.rcggs.datalake.core.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String message;
	private String type;

	public Message() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(getMessage());
		return builder.hashCode();
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Message))
			return false;
		final Message obj = (Message) that;
		return new EqualsBuilder().append(this.getMessage(), obj.getMessage())
				.isEquals();
	}
}