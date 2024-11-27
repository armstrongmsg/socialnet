package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
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
	private ApplicationFacade facade;
	private JsfExceptionHandler exceptionHandler;
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@PostConstruct
	public void initialize() {
		facade = applicationBean.getFacade();
		exceptionHandler = new JsfExceptionHandler();
	}
	
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

	public ContextBean getContextBean() {
		return contextBean;
	}

	public void setContextBean(ContextBean contextBean) {
		this.contextBean = contextBean;
	}
	
	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}
	
	public void addFriendship() {
		try {
			facade.addFriendship(contextBean.getCurrentSession().getUserToken(), username);
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException | InternalErrorException | FriendshipAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public void addFriendshipRequest() {
		try {
			facade.addFriendshipRequest(contextBean.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		} catch (FriendshipRequestAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public List<String> getSentFriendshipRequests() {
		try {
			List<FriendshipRequest> requests = 
					facade.getSentFriendshipRequests(contextBean.getCurrentSession().getUserToken());
			List<String> usernames = new ArrayList<String>();
			
			for (FriendshipRequest request : requests) {
				usernames.add(request.getRequested().getUsername());
			}
			
			return usernames;
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return null;
	}
	
	public List<String> getReceivedFriendshipRequests() {
		try {
			List<FriendshipRequest> requests = 
					facade.getReceivedFriendshipRequests(contextBean.getCurrentSession().getUserToken());
			List<String> usernames = new ArrayList<String>();
			
			for (FriendshipRequest request : requests) {
				usernames.add(request.getRequester().getUsername());
			}
			
			return usernames;
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return null;
	}
	
	public void accceptFriendshipRequest() {
		try {
			facade.acceptFriendshipRequest(contextBean.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		} catch (FriendshipRequestNotFound e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (FriendshipAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public void rejectFriendshipRequest() {
		try {
			facade.rejectFriendshipRequest(contextBean.getCurrentSession().getUserToken(), username);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (FriendshipRequestNotFound e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public List<UserSummary> getSelfFriends() {
		try {
			if (friends == null) {
				friends = new JsfConnector().getViewUserSummaries(
						facade.getSelfFriends(contextBean.getCurrentSession().getUserToken()));
			}
			
			return friends;
		} catch (AuthenticationException | UnauthorizedOperationException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<UserSummary>();
	}
	
	public List<UserSummary> getFriendRecommendations() {
		try {
			if (friendRecommendations == null) {
				String token = contextBean.getCurrentSession().getUserToken();
				friendRecommendations = new JsfConnector().getViewUserSummaries(facade.getUserRecommendations(token));
			}
			return friendRecommendations;
		} catch (UnauthorizedOperationException | AuthenticationException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<UserSummary>();
	}
	
	public void unfriend() {
		try {
			String token = contextBean.getCurrentSession().getUserToken();
			facade.unfriend(token, username);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (FriendshipNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
	}
}
