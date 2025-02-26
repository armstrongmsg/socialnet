package com.armstrongmsg.socialnet.model;

import java.util.Objects;

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
		this.id = requester.getUserId() + "-" + requested.getUserId();
		this.requester = requester;
		this.requested = requested;
	}

	public FriendshipRequest(String id, User requester, User requested) {
		this.id = id;
		this.requester = requester;
		this.requested = requested;
	}

	public String getId() {
		return id;
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

	@Override
	public int hashCode() {
		return Objects.hash(id, requested, requester);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FriendshipRequest other = (FriendshipRequest) obj;
		return Objects.equals(id, other.id) && 
				Objects.equals(requested.getUserId(), other.requested.getUserId()) && 
				Objects.equals(requester.getUserId(), other.requester.getUserId());
	}
}
