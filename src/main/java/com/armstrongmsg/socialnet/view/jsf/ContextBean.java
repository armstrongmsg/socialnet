package com.armstrongmsg.socialnet.view.jsf;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "contextBean", eager = true)
@SessionScoped
public class ContextBean {
	private String username;
	private String password;
	private ApplicationFacade facade = ApplicationFacade.getInstance();
	
	private UserSummary loggedUserSummary;
	private UserSummary viewUser;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public UserSummary getViewUser() {
		if (viewUser == null) {
			try {
				UserToken currentUserToken = SessionManager.getCurrentSession().getUserToken();
				this.viewUser = new JsfConnector().getViewUserSummary(facade.getSelf(currentUserToken));
			} catch (AuthenticationException e) {
				// FIXME treat this exception
				e.printStackTrace();
			} catch (UnauthorizedOperationException e) {
				// FIXME treat this exception
				e.printStackTrace();
			}
		}
		
		return viewUser;
	}

	public void setViewUser(UserSummary viewUser) {
		this.viewUser = viewUser;
	}
	
	public UserToken getUser() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return new UserToken("no context", "no context", "no context");
		}
		
		return SessionManager.getCurrentSession().getUserToken();
	}

	public String login() {
		try {
			Map<String, String> credentials = new HashMap<String, String>();
			credentials.put(AuthenticationParameters.USERNAME_KEY, username);
			credentials.put(AuthenticationParameters.PASSWORD_KEY, password);
			
			UserToken token = facade.login(credentials);
			boolean userIsAdmin = facade.userIsAdmin(username);
			SessionManager.startSession(token, userIsAdmin);
			
			if (userIsAdmin) {
				return new NavigationController().showAdminHome();
			} else {
				return new NavigationController().showUserHome();
			}
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} finally {
			username = null;
			password = null;
		}
		
		return new NavigationController().showUserHome();
	}
	
	public String logout() {
		SessionManager.setCurrentSession(null);
		this.viewUser = null;
		this.loggedUserSummary = null;
		return new NavigationController().showHome();
	}

	public boolean isLogged() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return false;
		}
		
		return SessionManager.getCurrentSession().isLogged();
	}

	public void setLogged(boolean logged) {
		Session session = SessionManager.getCurrentSession();
		
		if (session != null) {
			SessionManager.getCurrentSession().setLogged(logged);
		}
	}

	public boolean isAdmin() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return false;
		}
		
		return SessionManager.getCurrentSession().isAdmin();
	}

	public void setAdmin(boolean isAdmin) {
		Session session = SessionManager.getCurrentSession();
		
		if (session != null) {
			SessionManager.getCurrentSession().setAdmin(isAdmin);			
		}
	}
	
	public boolean getCanAddAsFriend() {
		return !viewUserIsLoggedUser() && !viewUserIsFriend();
	}
	
	private boolean viewUserIsFriend() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return facade.isFriend(token, this.viewUser.getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}
	
	public boolean getCanFollow() {
		return !viewUserIsLoggedUser() && !viewUserIsFollowed();
	}

	private boolean viewUserIsFollowed() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return facade.follows(token, this.viewUser.getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}

	private boolean viewUserIsLoggedUser() {
		try {
			if (loggedUserSummary == null) {
				UserToken loggedUser = SessionManager.getCurrentSession().getUserToken();
				loggedUserSummary = new JsfConnector().getViewUserSummary(facade.getSelf(loggedUser));
			}
			return loggedUserSummary.equals(this.viewUser);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
			return false;
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}
}
