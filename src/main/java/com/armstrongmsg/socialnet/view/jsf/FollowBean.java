package com.armstrongmsg.socialnet.view.jsf;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "followBean", eager = true)
@RequestScoped
public class FollowBean {
	private User follower;
	private User followed;
	
	private String username;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
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
	
	public void addFollowAdmin() {
		try {
			facade.addFollowAdmin(SessionManager.getCurrentSession().getUserToken(), 
					SessionManager.getCurrentSession().getUserToken().getUserId(), followed.getUserId());
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
	}
	
	public void addFollow() {
		try {
			facade.addFollow(SessionManager.getCurrentSession().getUserToken(), getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			//FIXME treat exception
		}
	}
	
	public List<UserSummary> getSelfFollows() {
		try {
			return new JsfConnector().getViewUserSummaries(
					facade.getFollowedUsers(SessionManager.getCurrentSession().getUserToken()));
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
	
	public List<UserSummary> getFollowRecommendations() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return new JsfConnector().getViewUserSummaries(facade.getUserRecommendations(token));
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}
}
