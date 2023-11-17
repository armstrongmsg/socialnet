package com.armstrongmsg.socialnet.model;

public class User {
	private String userId;
	private String username;
	private String password;
	private Profile profile;
	
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
}
