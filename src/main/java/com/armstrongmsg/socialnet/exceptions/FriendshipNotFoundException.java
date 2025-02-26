package com.armstrongmsg.socialnet.exceptions;

public class FriendshipNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FriendshipNotFoundException() {
		
	}
	
	public FriendshipNotFoundException(String message) {
		super(message);
	}
}
