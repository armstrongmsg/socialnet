package com.armstrongmsg.socialnet.model.authentication;

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
}
