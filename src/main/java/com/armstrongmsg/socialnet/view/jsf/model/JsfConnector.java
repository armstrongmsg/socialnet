package com.armstrongmsg.socialnet.view.jsf.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.primefaces.model.StreamedContent;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class JsfConnector {
	private PrimefacesConnector primefacesConnector;
	
	public JsfConnector() {
		this.primefacesConnector = new PrimefacesConnector();
	}
	
	public User getViewUser(com.armstrongmsg.socialnet.model.User modelUser) {
		return new User(modelUser.getUserId(), modelUser.getUsername(), 
				modelUser.getProfile().getDescription());
	}
	
	public List<User> getViewUsers(List<com.armstrongmsg.socialnet.model.User> modelUsers) {
		List<User> newViewUsers = new ArrayList<User>();
		
		for (com.armstrongmsg.socialnet.model.User modelUser : modelUsers) {
			newViewUsers.add(getViewUser(modelUser));
		}
		
		return newViewUsers;
	}
	
	private String toViewDate(long timestamp) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timestamp);
		return String.format("%02d/%02d/%04d - %02d:%02d:%02d", 
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
	}
	
	private String getRemoteMediaStorageProperty() {
		try {
			return PropertiesUtil.getInstance().
					getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE);
		} catch (FatalErrorException e) {
			return "false";
			// TODO Log
		}	
	}
	
	public Post getViewPost(com.armstrongmsg.socialnet.model.Post modelPost) {
		StreamedContent content = this.primefacesConnector.getOrDefaultStreamContent(modelPost.getPicture());
		String picturePath = null;
		
		if (modelPost.getPicture() != null) {
			String remoteMediaStorageProperty = getRemoteMediaStorageProperty();
			
			// TODO test
			if (remoteMediaStorageProperty.equals("true")) {
				picturePath = modelPost.getPicture().getPath();
			} else {
				picturePath = this.primefacesConnector.convertPicturePathToWebFormat(modelPost.getPicture().getPath());
			}
		}
		
		return new Post(modelPost.getId(), modelPost.getTitle(), toViewDate(modelPost.getTimestamp()), 
				modelPost.getContent(), modelPost.getVisibility().getValue(), content, picturePath);
	}

	public List<Post> getViewPosts(List<com.armstrongmsg.socialnet.model.Post> modelPosts) {
		List<Post> newViewPosts = new ArrayList<Post>();
		
		for (com.armstrongmsg.socialnet.model.Post modelPost : modelPosts) {
			newViewPosts.add(getViewPost(modelPost));
		}
		
		return newViewPosts;
	}

	public List<UserSummary> getViewUserSummaries(List<com.armstrongmsg.socialnet.model.UserSummary> modelUserSummaries) {
		List<UserSummary> viewUserSummaries = new ArrayList<UserSummary>();
		
		for (com.armstrongmsg.socialnet.model.UserSummary modelUserSummary : modelUserSummaries) {
			viewUserSummaries.add(getViewUserSummary(modelUserSummary));
		}
		
		return viewUserSummaries;
	}

	public UserSummary getViewUserSummary(com.armstrongmsg.socialnet.model.UserSummary modelUserSummary) {
		StreamedContent profilePicStreamedContent = this.primefacesConnector.
					getOrDefaultStreamContent(modelUserSummary.getProfilePicture());
		String profilePicturePath = null;
		String remoteMediaStorageProperty = getRemoteMediaStorageProperty();
		
		// TODO test
		if (remoteMediaStorageProperty.equals("true")) {
			profilePicturePath = modelUserSummary.getProfilePicture().getPath();
		} else {
			profilePicturePath = this.primefacesConnector.
					convertPicturePathToWebFormat(modelUserSummary.getProfilePicture().getPath());
		}
		
		return new UserSummary(modelUserSummary.getUsername(), modelUserSummary.getProfileDescription(), 
				profilePicStreamedContent, profilePicturePath);
	}
}
