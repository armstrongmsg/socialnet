package com.armstrongmsg.socialnet.model;

import java.util.Objects;

public class UserView {
	private String username;
	private String profileDescription;
	private String profilePicId;
	private String profilePicPath;

	public UserView(String username, String profileDescription, String profilePicId, String profilePicPath) {
		this.username = username;
		this.profileDescription = profileDescription;
		this.setProfilePicId(profilePicId);
		this.setProfilePicPath(profilePicPath);
	}
	
	public UserView(String username, String profileDescription, Picture profilePicture) {
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

	public String getProfilePicId() {
		return profilePicId;
	}

	public void setProfilePicId(String profilePicId) {
		this.profilePicId = profilePicId;
	}

	public String getProfilePicPath() {
		return profilePicPath;
	}

	public void setProfilePicPath(String profilePicPath) {
		this.profilePicPath = profilePicPath;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(profileDescription, profilePicId, profilePicPath, username);
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
		return Objects.equals(profileDescription, other.profileDescription)
				&& Objects.equals(profilePicId, other.profilePicId)
				&& Objects.equals(profilePicPath, other.profilePicPath) && Objects.equals(username, other.username);
	}
}
