package com.armstrongmsg.socialnet.exceptions;

public class FollowAlreadyExistsException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FollowAlreadyExistsException() {
		
	}
	
	public FollowAlreadyExistsException(String message) {
		super(message);
	}
}
