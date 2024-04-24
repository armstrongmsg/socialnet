package com.armstrongmsg.socialnet.view.jsf;

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

@ManagedBean(name = "friendshipBean", eager = true)
@RequestScoped
public class FriendshipBean {
	private User user1;
	private User user2;
	
	private String username;
	
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
	
	// TODO remove
	public void addFriendshipAdmin() {
		try {
			facade.addFriendshipAdmin(SessionManager.getCurrentSession().getUserToken(), 
					SessionManager.getCurrentSession().getUserToken().getUserId(), user2.getUserId());
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME handle this exception
		}
	}
	
	public void addFriendship() {
		try {
			facade.addFriendship(SessionManager.getCurrentSession().getUserToken(), username);
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME handle this exception
		}
	}
	
	public List<UserSummary> getSelfFriends() {
		try {
			return new JsfConnector().getViewUserSummaries(
					facade.getSelfFriends(SessionManager.getCurrentSession().getUserToken()));
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat exception
		}
		
		return null;
	}
	
	public List<UserSummary> getFriendRecommendations() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return new JsfConnector().getViewUserSummaries(facade.getUserRecommendations(token));
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
		
		// FIXME
		return null;
	}
}
