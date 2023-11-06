package com.armstrongmsg.socialnet.model;

public class User {
	private String username;
	private String userId;
	private Profile profile;
	
	public User(String username, String userId, Profile profile) {
		this.username = username;
		this.userId = userId;
		this.profile = profile;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public Profile getProfile() {
		return profile;
	}
}
