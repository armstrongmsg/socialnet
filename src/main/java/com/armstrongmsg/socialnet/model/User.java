package com.armstrongmsg.socialnet.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
public class User {
	@Column(name = "userid")
	@Id
	private String userId;
	@Column
	private String username;
	@Column
	private String password;
	@Embedded
	private Profile profile;
	
	public User() {
		
	}
	
	public User(String userId, String username, String password, Profile profile) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.profile = profile;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Profile getProfile() {
		return profile;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(userId, other.userId);
	}
}
