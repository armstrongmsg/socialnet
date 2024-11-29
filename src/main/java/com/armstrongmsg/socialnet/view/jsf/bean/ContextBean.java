package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.NavigationController;
import com.armstrongmsg.socialnet.view.jsf.Session;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

// TODO refactor
@ManagedBean(name = "contextBean", eager = true)
@SessionScoped
public class ContextBean {
	private ApplicationFacade facade;
	private String username;
	private String password;
	private boolean loginError;
	private Session session;
	private JsfExceptionHandler exceptionHandler;
	
	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@PostConstruct
	public void initialize() {
		facade = applicationBean.getFacade();
		exceptionHandler = new JsfExceptionHandler();
	}
	
	public ContextBean() {
		
	}
	
	ContextBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
		this.facade = applicationBean.getFacade();
	}
	
	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
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

	public boolean isLoginError() {
		return loginError;
	}

	public void setLoginError(boolean loginError) {
		this.loginError = loginError;
	}
	
	public Session getCurrentSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public UserSummary getViewUser() {
		UserSummary currentViewUser = this.session.getCurrentViewUser();
		
		if (currentViewUser == null) {
			try {
				String currentUserToken = this.session.getUserToken();
				currentViewUser = new JsfConnector(facade, currentUserToken).getViewUserSummary(facade.getSelf(currentUserToken));
				this.session.setCurrentViewUser(currentViewUser);
			} catch (AuthenticationException e) {
				this.exceptionHandler.handle(e);
			} catch (InternalErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return currentViewUser;
	}

	public void setViewUser(UserSummary viewUser) {
		this.session.setCurrentViewUser(viewUser);
	}
	
	public String getUser() {
		if (this.session == null) {
			return "";
		}
		
		return this.session.getUserToken();
	}

	public String login() {
		try {
			Map<String, String> credentials = new HashMap<String, String>();
			credentials.put(AuthenticationParameters.USERNAME_KEY, username);
			credentials.put(AuthenticationParameters.PASSWORD_KEY, password);
			
			String token = facade.login(credentials);
			boolean userIsAdmin = facade.userIsAdmin(username);

			this.session = new Session(token);
			this.session.setAdmin(userIsAdmin);
			this.session.setLogged(true);
			
			if (userIsAdmin) {
				return new NavigationController().showAdminHome();
			} else {
				return new NavigationController().showUserHome();
			}
		} catch (AuthenticationException | InternalErrorException | UserNotFoundException e) {
			setLoginError(true);
			return new NavigationController().showHome();
		} finally {
			username = null;
			password = null;
		}
	}
	
	public String logout() {
		this.session = null;
		return new NavigationController().showHome();
	}

	public boolean isLogged() {
		if (this.session == null) {
			return false;
		}
		
		return this.session.isLogged();
	}

	public void setLogged(boolean logged) {
		if (this.session != null) {
			this.session.setLogged(logged);
		}
	}

	public boolean isAdmin() {
		if (this.session == null) {
			return false;
		}
		
		return this.session.isAdmin();
	}

	public void setAdmin(boolean isAdmin) {
		if (this.session != null) {
			this.session.setAdmin(isAdmin);			
		}
	}
}
