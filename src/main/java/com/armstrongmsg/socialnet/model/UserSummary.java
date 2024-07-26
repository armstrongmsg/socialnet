package com.armstrongmsg.socialnet.model;

import java.util.Objects;

// TODO maybe change this class's name to UserView
public class UserSummary {
	private String username;
	private String profileDescription;
	private byte[] profilePic;

	public UserSummary(String username, String profileDescription, byte[] profilePic) {
		this.username = username;
		this.profileDescription = profileDescription;
		this.profilePic = profilePic;
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

	public byte[] getProfilePic() {
		return this.profilePic;
	}
	
	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
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
