package com.armstrongmsg.socialnet.model;

import java.util.Objects;

// TODO maybe change this class's name to UserView
public class UserSummary {
	private String username;
	private String profileDescription;
	private Picture profilePicture;

	public UserSummary(String username, String profileDescription, Picture profilePicture) {
		this.username = username;
		this.profileDescription = profileDescription;
		this.profilePicture = profilePicture;
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

	public Picture getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(Picture profilePicture) {
		this.profilePicture = profilePicture;
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
		UserSummary other = (UserSummary) obj;
		return Objects.equals(profileDescription, other.profileDescription) && Objects.equals(username, other.username);
	}
}
