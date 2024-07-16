package com.armstrongmsg.socialnet.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Friendship_Requests")
public class FriendshipRequest {
	@Column
	@Id
	private String id;
	@OneToOne
	private User requester;
	@OneToOne
	private User requested;
	
	public FriendshipRequest() {
		
	}
	
	public FriendshipRequest(User requester, User requested) {
		this.id = UUID.randomUUID().toString();
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
