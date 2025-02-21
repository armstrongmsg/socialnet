package com.armstrongmsg.socialnet.exceptions;

public class FriendshipRequestNotFound extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FriendshipRequestNotFound() {
		
	}
	
	public FriendshipRequestNotFound(String message) {
		super(message);
	}
}
