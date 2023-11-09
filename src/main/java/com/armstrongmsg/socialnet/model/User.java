package com.armstrongmsg.socialnet.model;

public class User {
	private String username;
	private String userId;
	private String password;
	private Profile profile;
	
	public User(String username, String userId, String password, Profile profile) {
		this.username = username;
		this.userId = userId;
		this.password = password;
		this.profile = profile;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Profile getProfile() {
		return profile;
	}
}
