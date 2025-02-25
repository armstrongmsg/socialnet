package com.armstrongmsg.socialnet.exceptions;

public class InternalErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InternalErrorException() {
		
	}
	
	public InternalErrorException(Exception cause) {
		super(cause);
	}
	
	public InternalErrorException(String message) {
		super(message);
	}
}
