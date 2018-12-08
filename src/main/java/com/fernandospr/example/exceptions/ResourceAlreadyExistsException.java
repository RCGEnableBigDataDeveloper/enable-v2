package com.fernandospr.example.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceAlreadyExistsException(String message) {
		super(message);
	}

	public String getCode() { return "RESOURCE_ALREADY_EXISTS"; }
}
