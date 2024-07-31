package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.SessionManager;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "adminBean", eager = true)
@SessionScoped
public class AdminBean {
	private String userId;
	private String username;
	private String password;
	private String profileDescription;

	private User user;
	private List<User> users;
	
	private UserSummary userSummary;

	private static ApplicationFacade facade = ApplicationFacade.getInstance();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserSummary getUserSummary() {
		return userSummary;
	}

	public void setUserSummary(UserSummary userSummary) {
		this.userSummary = userSummary;
	}

	public List<User> getUsers() {
		if (users == null) {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			try {
				users = new JsfConnector().getViewUsers(facade.getUsers(token));
			} catch (UnauthorizedOperationException e) {
				// FIXME Treat this exception
			} catch (AuthenticationException e) {
				// FIXME Treat this exception
				e.printStackTrace();
			}
		}

		return users;
	}
	
	public List<UserSummary> getUserSummaries() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return new JsfConnector().getViewUserSummaries(facade.getUserSummaries(token));
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
			return new ArrayList<UserSummary>();
		}
	}

	public String addUser() {
		UserToken token = SessionManager.getCurrentSession().getUserToken();
		try {
			facade.addUser(token, userId, username, getProfileDescription());
			users = new JsfConnector().getViewUsers(facade.getUsers(token));
		} catch (UnauthorizedOperationException e) {
			// FIXME Treat this exception
		} catch (AuthenticationException e) {
			// FIXME Treat this exception
			e.printStackTrace();
		}
		return null;
	}

	public String editUser() {
		getUser().setCanEdit(true);
		return null;
	}

	public String removeUser() {
		UserToken token = SessionManager.getCurrentSession().getUserToken();
		try {
			facade.removeUser(token, getUser().getUserId());
			users = new JsfConnector().getViewUsers(facade.getUsers(token));
		} catch (UnauthorizedOperationException e) {
			// FIXME Treat this exception
		} catch (AuthenticationException e) {
			// FIXME Treat this exception
			e.printStackTrace();
		} catch (UserNotFoundException e) {
			// FIXME Treat this exception
			e.printStackTrace();
		}
		return null;
	}

	public String saveUsers() {
		for (User g : getUsers()) {
			g.setCanEdit(false);
		}

		return null;
	}
	
	public List<User> getFriends() {
		try {
			return new JsfConnector().getViewUsers(
					facade.getFriends(
							SessionManager.getCurrentSession().getUserToken(), user.getUserId()));
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat exception
		}
		return null;
	}
	
	public List<User> getFollows() {
		try {
			return new JsfConnector().getViewUsers(
					facade.getFollowedUsers(SessionManager.getCurrentSession().getUserToken(), 
							user.getUserId()));
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException e) {
			// FIXME treat exception
			return new ArrayList<User>();
		}
	}
}