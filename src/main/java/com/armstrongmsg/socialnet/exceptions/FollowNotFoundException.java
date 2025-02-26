package com.armstrongmsg.socialnet.exceptions;

public class FollowNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FollowNotFoundException() {
		
	}
	
	public FollowNotFoundException(String message) {
		super(message);
	}
}
