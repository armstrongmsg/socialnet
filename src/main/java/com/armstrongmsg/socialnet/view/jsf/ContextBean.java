package com.armstrongmsg.socialnet.view.jsf;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "contextBean", eager = true)
@SessionScoped
public class ContextBean {
	private String username;
	private String password;
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	private UserSummary viewUser; 
	
	public UserToken getUser() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return new UserToken("no context", "no context", "no context");
		}
		
		return SessionManager.getCurrentSession().getUserToken();
	}

	public String login() {
		Map<String, String> credentials = new HashMap<String, String>();
		// FIXME constant
		credentials.put("USER_ID", username);
		// FIXME constant
		credentials.put("PASSWORD", password);
		try {
			UserToken token = facade.login(credentials);
			Session session = new Session(token);
			session.setAdmin(facade.userIsAdmin(username));
			session.setLogged(true);
			SessionManager.setCurrentSession(session);
			if (session.isAdmin()) {
				// FIXME constant
				return new NavigationController().showPageById("admin-home");
			} else {
				// FIXME constant
				return new NavigationController().showPageById("user-home");
			}
					
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		}
		
		// FIXME constant
		return "user-home";
	}
	
	public String logout() {
		SessionManager.setCurrentSession(null);
		// FIXME constant
		return new NavigationController().showPageById("home");
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

	public UserSummary getViewUser() {
		if (viewUser == null) {
			// FIXME should not depend on token structure
			UserToken currentUserToken = SessionManager.getCurrentSession().getUserToken();
			this.viewUser = new UserSummary(currentUserToken.getUsername(),
					currentUserToken.getProfileDescription());
		}
		
		return viewUser;
	}

	public void setViewUser(UserSummary viewUser) {
		this.viewUser = viewUser;
	}
}
