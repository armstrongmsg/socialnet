package com.armstrongmsg.socialnet.view.jsf.model;

import java.util.Objects;

public class UserView {
	private String username;
	private String profileDescription;
	private String profilePicPath;
	
	public UserView(String username, String profileDescription, String profilePicPath) {
		this.username = username;
		this.profileDescription = profileDescription;
		this.profilePicPath = profilePicPath;
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

	public String getCachedPicturePath() {
		return profilePicPath;
	}

	public void setCachedPicturePath(String profilePicPath) {
		this.profilePicPath = profilePicPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(profileDescription, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserView other = (UserView) obj;
		return Objects.equals(profileDescription, other.profileDescription) && Objects.equals(username, other.username);
	}
}
