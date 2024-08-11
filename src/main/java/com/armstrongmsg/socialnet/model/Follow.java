package com.armstrongmsg.socialnet.model;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Follows")
public class Follow extends Relationship {
	@Column
	@Id
	private String id;
	@OneToOne
	private User follower;
	@OneToOne
	private User followed;
	
	public Follow() {
		
	}
	
	public Follow(User follower, User followed) {
		this.id = UUID.randomUUID().toString();
		this.follower = follower;
		this.followed = followed;
	}
	
	public Follow(String id, User follower, User followed) {
		this.id = id;
		this.follower = follower;
		this.followed = followed;
	}

	public String getId() {
		return id;
	}

	public User getFollower() {
		return follower;
	}

	public User getFollowed() {
		return followed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Follow other = (Follow) obj;
		return Objects.equals(id, other.id) && 
				Objects.equals(follower.getUserId(), other.follower.getUserId()) && 
				Objects.equals(followed.getUserId(), other.followed.getUserId());
	}
}
