package com.armstrongmsg.socialnet.exceptions;

public class MediaNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public MediaNotFoundException() {
		
	}
	
	public MediaNotFoundException(String message) {
		super(message);
	}
}
