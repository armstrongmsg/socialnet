package com.armstrongmsg.socialnet.exceptions;

public class FatalErrorException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FatalErrorException() {
		
	}
	
	public FatalErrorException(String message) {
		super(message);
	}
}
