package com.armstrongmsg.socialnet.view.jsf.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.armstrongmsg.socialnet.constants.SystemConstants;

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

	public StreamedContent getProfilePic() throws FileNotFoundException {
		if (profilePic == null) {
			String defaultProfilePicPath = Thread.currentThread().getContextClassLoader().
					getResource("").getPath() + File.separator + SystemConstants.DEFAULT_PROFILE_PIC;
			InputStream profilePicStream = new FileInputStream(new File(defaultProfilePicPath));
			return DefaultStreamedContent.
					builder().
					contentType("image/jpeg").
					stream(() -> profilePicStream).
					build();
		} else {
			InputStream profilePicStream = new ByteArrayInputStream(profilePic);
			return DefaultStreamedContent.
					builder().
					contentType("image/jpeg").
					stream(() -> profilePicStream).
					build();
		}
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
