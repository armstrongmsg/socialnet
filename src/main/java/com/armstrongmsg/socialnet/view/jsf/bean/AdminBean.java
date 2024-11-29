package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

// TODO refactor
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
	
	public List<User> getUsers() {
		if (users == null) {
			String token = contextBean.getCurrentSession().getUserToken();
			try {
				users = new JsfConnector(facade, token).getViewUsers(facade.getUsers(token));
			} catch (UnauthorizedOperationException e) {
				this.exceptionHandler.handle(e);
			} catch (AuthenticationException e) {
				this.exceptionHandler.handle(e);
			} catch (InternalErrorException e) {
				this.exceptionHandler.handle(e);
			}
		}

		return users;
	}
	
	public List<UserSummary> getUserSummaries() {
		try {
			String token = contextBean.getCurrentSession().getUserToken();
			return new JsfConnector(facade, token).getViewUserSummaries(facade.getUserSummaries(token));
		} catch (UnauthorizedOperationException | AuthenticationException | InternalErrorException | MediaNotFoundException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<UserSummary>();
	}

	public String addUser() {
		String token = contextBean.getCurrentSession().getUserToken();
		try {
			facade.addUser(token, userId, username, getProfileDescription());
			users = new JsfConnector(facade, token).getViewUsers(facade.getUsers(token));
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (UserAlreadyExistsException e) {
			this.exceptionHandler.handle(e);
		}
		
		return null;
	}

	public String editUser() {
		getUser().setCanEdit(true);
		return null;
	}

	public String removeUser() {
		String token = contextBean.getCurrentSession().getUserToken();
		try {
			facade.removeUser(token, getUser().getUserId());
			users = new JsfConnector(facade, token).getViewUsers(facade.getUsers(token));
		} catch (UnauthorizedOperationException e) {
			this.exceptionHandler.handle(e);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (UserNotFoundException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
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
			return new JsfConnector(facade, contextBean.getCurrentSession().getUserToken()).getViewUsers(
					facade.getFriends(
							contextBean.getCurrentSession().getUserToken(), user.getUserId()));
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return null;
	}
	
	public List<User> getFollows() {
		try {
			return new JsfConnector(facade, contextBean.getCurrentSession().getUserToken()).getViewUsers(
					facade.getFollowedUsers(contextBean.getCurrentSession().getUserToken(), 
							user.getUserId()));
		} catch (UnauthorizedOperationException | AuthenticationException | UserNotFoundException | InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return new ArrayList<User>();
	}
}
