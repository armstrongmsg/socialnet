package com.armstrongmsg.socialnet.exceptions;

public class PostNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PostNotFoundException() {
		
	}
	
	public PostNotFoundException(String message) {
		super(message);
	}
}
