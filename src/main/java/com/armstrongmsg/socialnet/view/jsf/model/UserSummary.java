package com.armstrongmsg.socialnet.view.jsf.model;

import java.util.Objects;

import org.primefaces.model.StreamedContent;

public class UserSummary {
	private String username;
	private String profileDescription;
	private String profilePicPath;
	private StreamedContent profilePicture;
	
	public UserSummary(String username, String profileDescription, StreamedContent profilePicture, String profilePicPath) {
		this.username = username;
		this.profileDescription = profileDescription;
		this.profilePicPath = profilePicPath;
		this.setProfilePicture(profilePicture);
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

	public StreamedContent getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(StreamedContent profilePicture) {
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
