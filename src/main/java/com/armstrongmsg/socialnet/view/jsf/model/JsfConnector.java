package com.armstrongmsg.socialnet.view.jsf.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;

public class JsfConnector {
	private ApplicationFacade facade;
	private String token;
	
	public JsfConnector(ApplicationFacade facade, String token) {
		this.token = token;
		this.facade = facade;
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
	
	public Post getViewPost(com.armstrongmsg.socialnet.model.Post modelPost) {
		String picturePath = null;

		for (String id : modelPost.getMediaIds()) {
			try {
				picturePath = facade.getMediaUri(token, id);
			} catch (AuthenticationException e) {
			} catch (MediaNotFoundException e) {
			} catch (InternalErrorException e) {
			} catch (UnauthorizedOperationException e) {
			}
		}
		
		return new Post(modelPost.getId(), modelPost.getTitle(), toViewDate(modelPost.getTimestamp()), 
				modelPost.getContent(), modelPost.getVisibility().getValue(), picturePath);
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
		String profilePicturePath = null;
		try {
			profilePicturePath = facade.getMediaUri(token, modelUserSummary.getProfilePicId());
		} catch (AuthenticationException e) {
		} catch (MediaNotFoundException e) {
		} catch (InternalErrorException e) {
		} catch (UnauthorizedOperationException e) {
		}
		return new UserSummary(modelUserSummary.getUsername(), modelUserSummary.getProfileDescription(), profilePicturePath);
	}
}
