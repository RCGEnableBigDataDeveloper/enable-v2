package com.rcggs.datalake.core.model;

public enum MessageType {
	
	ERROR("error"), INFO("info");

	private String messageType;

	private MessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessage() {
		return messageType;
	}
}
