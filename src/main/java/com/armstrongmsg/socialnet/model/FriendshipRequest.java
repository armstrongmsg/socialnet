package com.armstrongmsg.socialnet.model;

public class FriendshipRequest {
	private User requester;
	private User requested;
	
	public FriendshipRequest(User requester, User requested) {
		this.requester = requester;
		this.requested = requested;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public User getRequested() {
		return requested;
	}

	public void setRequested(User requested) {
		this.requested = requested;
	}
}
