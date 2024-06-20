package com.armstrongmsg.socialnet.model.authentication;

import java.util.Objects;

public class UserToken {
	private String userId;
	private String username;
	private String profileDescription;
	
	public UserToken(String userId, String username, String profileDescription) {
		this.setUserId(userId);
		this.username = username;
		this.profileDescription = profileDescription;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getProfileDescription() {
		return profileDescription;
	}
	
	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(profileDescription, userId, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserToken other = (UserToken) obj;
		return Objects.equals(profileDescription, other.profileDescription) && Objects.equals(userId, other.userId)
				&& Objects.equals(username, other.username);
	}
}
