package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "followBean", eager = true)
@RequestScoped
public class FollowBean {
	private User follower;
	private User followed;
	private String username;
	private List<UserSummary> follows;
	private List<UserSummary> followRecommendations;
	private ApplicationFacade facade;
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@PostConstruct
	public void initialize() {
		facade = applicationBean.getFacade();
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
					getContextBean().getCurrentSession().getUserToken().getUserId(), followed.getUserId());
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat exception
		}
	}
	
	public void addFollow() {
		try {
			facade.addFollow(getContextBean().getCurrentSession().getUserToken(), getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException | UserNotFoundException e) {
			//FIXME treat exception
		}
	}
	
	public List<UserSummary> getSelfFollows() {
		try {
			if (follows == null) {
				follows = new JsfConnector().getViewUserSummaries(
						facade.getFollowedUsers(getContextBean().getCurrentSession().getUserToken()));
			}
			
			return follows;
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
	
	public List<UserSummary> getFollowRecommendations() {
		try {
			if (followRecommendations == null) {
				UserToken token = getContextBean().getCurrentSession().getUserToken();
				followRecommendations = new JsfConnector().getViewUserSummaries(facade.getFollowRecommendations(token));
			}
			
			return followRecommendations;
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
	
	public void unfollow() {
		try {
			UserToken token = getContextBean().getCurrentSession().getUserToken();
			facade.unfollow(token, username);
		} catch (AuthenticationException e) {
			// FIXME treat exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}
}
