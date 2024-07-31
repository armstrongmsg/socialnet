package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.view.jsf.SessionManager;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "friendshipBean", eager = true)
@RequestScoped
public class FriendshipBean {
	private User user1;
	private User user2;
	
	private String username;
	private List<UserSummary> friends;
	private List<UserSummary> friendRecommendations;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	public User getUser1() {
		return user1;
	}
	
	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void addFriendship() {
		try {
			facade.addFriendship(SessionManager.getCurrentSession().getUserToken(), username);
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat this exception
		}
	}
	
	public void addFriendshipRequest() {
		try {
			facade.addFriendshipRequest(SessionManager.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		} catch (UserNotFoundException e) {
			// FIXME treat this exception
		}
	}
	
	public List<String> getSentFriendshipRequests() {
		try {
			List<FriendshipRequest> requests = 
					facade.getSentFriendshipRequests(SessionManager.getCurrentSession().getUserToken());
			List<String> usernames = new ArrayList<String>();
			
			for (FriendshipRequest request : requests) {
				usernames.add(request.getRequested().getUsername());
			}
			
			return usernames;
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return null;
	}
	
	public List<String> getReceivedFriendshipRequests() {
		try {
			List<FriendshipRequest> requests = 
					facade.getReceivedFriendshipRequests(SessionManager.getCurrentSession().getUserToken());
			List<String> usernames = new ArrayList<String>();
			
			for (FriendshipRequest request : requests) {
				usernames.add(request.getRequester().getUsername());
			}
			
			return usernames;
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return null;
	}
	
	public void accceptFriendshipRequest() {
		try {
			facade.acceptFriendshipRequest(SessionManager.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		} catch (UserNotFoundException e) {
			// FIXME treat this exception
		}
	}
	
	public void rejectFriendshipRequest() {
		try {
			facade.rejectFriendshipRequest(SessionManager.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
	}
	
	public List<UserSummary> getSelfFriends() {
		try {
			if (friends == null) {
				friends = new JsfConnector().getViewUserSummaries(
						facade.getSelfFriends(SessionManager.getCurrentSession().getUserToken()));
			}
			
			return friends;
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
	
	public List<UserSummary> getFriendRecommendations() {
		try {
			if (friendRecommendations == null) {
				UserToken token = SessionManager.getCurrentSession().getUserToken();
				friendRecommendations = new JsfConnector().getViewUserSummaries(facade.getUserRecommendations(token));
			}
			return friendRecommendations;
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
	
	public void unfriend() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			facade.unfriend(token, username);
		} catch (AuthenticationException e) {
			// FIXME treat exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}
}
