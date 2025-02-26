package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserView;

// TODO refactor
@ManagedBean(name = "followBean", eager = true)
@RequestScoped
public class FollowBean {
	private User follower;
	private User followed;
	private String username;
	private List<UserView> follows;
	private List<UserView> followRecommendations;
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
	
	public User getFollower() {
		return follower;
	}
	
	public void setFollower(User follower) {
		this.follower = follower;
	}
	
	public User getFollowed() {
		return followed;
	}
	
	public void setFollowed(User followed) {
		this.followed = followed;
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
	
	public void addFollowAdmin() {
		try {
			facade.addFollowAdmin(getContextBean().getCurrentSession().getUserToken(), 
					getContextBean().getCurrentSession().getUserToken(), followed.getUserId());
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException | InternalErrorException | FollowAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public void addFollow() {
		try {
			facade.addFollow(getContextBean().getCurrentSession().getUserToken(), getUsername());
		} catch (AuthenticationException | UserNotFoundException | InternalErrorException | FollowAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		}
	}
	
	public List<UserView> getSelfFollows() {
		try {
			if (follows == null) {
				follows = new JsfConnector(facade, contextBean.getCurrentSession().getUserToken()).getViewUserSummaries(
						facade.getFollowedUsers(getContextBean().getCurrentSession().getUserToken()));
			}
			
			return follows;
		} catch (AuthenticationException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<UserView>();
	}
	
	public List<UserView> getFollowRecommendations() {
		try {
			if (followRecommendations == null) {
				String token = getContextBean().getCurrentSession().getUserToken();
				followRecommendations = new JsfConnector(facade, token).getViewUserSummaries(facade.getFollowRecommendations(token));
			}
			
			return followRecommendations;
		} catch (AuthenticationException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<UserView>();
	}
	
	public void unfollow() {
		try {
			String token = getContextBean().getCurrentSession().getUserToken();
			facade.unfollow(token, username);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (FollowNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
	}
}
