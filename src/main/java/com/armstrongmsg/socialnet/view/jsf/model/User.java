package com.armstrongmsg.socialnet.view.jsf.model;

public class User {
	private String userId;
	private String username;
	private String profileDescription;
	private boolean canEdit;
	
	public User(String userId, String username, String profileDescription) {
		this.userId = userId;
		this.username = username;
		this.setProfileDescription(profileDescription);
	}

	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
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

	public void setCanEdit(boolean b) {
		this.canEdit = b;
	}
	
	public boolean getCanEdit() {
		return this.canEdit;
	}
}
