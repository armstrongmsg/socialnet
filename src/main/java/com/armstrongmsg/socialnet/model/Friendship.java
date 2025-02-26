package com.armstrongmsg.socialnet.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Friendships")
public class Friendship extends Relationship {
	@Column
	@Id
	private String id;
	@OneToOne
	private User friend1;
	@OneToOne
	private User friend2;
	
	public Friendship() {
		
	}
	
	public Friendship(String id, User friend1, User friend2) {
		this.id = id;
		this.friend1 = friend1;
		this.friend2 = friend2;
	}
	
	public Friendship(User friend1, User friend2) {
		this.id = friend1.getUserId() + "-" + friend2.getUserId();
		this.friend1 = friend1;
		this.friend2 = friend2;
	}

	public String getId() {
		return id;
	}
	
	public User getFriend1() {
		return friend1;
	}

	public User getFriend2() {
		return friend2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friendship other = (Friendship) obj;
		return Objects.equals(getId(), other.getId()) && 
				Objects.equals(friend1.getUserId(), other.friend1.getUserId()) && 
				Objects.equals(friend2.getUserId(), other.friend2.getUserId());
	}
}
