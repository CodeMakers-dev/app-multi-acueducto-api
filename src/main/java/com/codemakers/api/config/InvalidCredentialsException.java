package com.codemakers.api.config;

public class InvalidCredentialsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException(String message) {
        super(message);
    }
}
