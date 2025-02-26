package com.armstrongmsg.socialnet.exceptions;

public class InvalidParameterException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidParameterException() {
		
	}
	
	public InvalidParameterException(String message) {
		super(message);
	}
}
