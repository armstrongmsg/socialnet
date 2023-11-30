package com.armstrongmsg.socialnet.view.jsf.model;

public class UserSummary {
	private String username;
	private String profileDescription;
	
	public UserSummary(String username, String profileDescription) {
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
}
