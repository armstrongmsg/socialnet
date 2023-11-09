package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "contextBean", eager = true)
@SessionScoped
public class ContextBean {
	private String username;
	private String password;
	private boolean logged;
	private boolean admin;
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	private User user;

	public User getUser() {
		if (user == null) {
			return new User("no context", "no context", "no context");
		}
		
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String login() {
		this.user = new JsfConnector().getViewUser(facade.validateCredentials(username, password));
		this.setAdmin(facade.userIsAdmin(this.user.getUserId()));
		this.logged = true;
		// TODO error handling
		// FIXME
		return "user-home";
	}
	
	public void logout() {
		this.user = null;
		this.setAdmin(false);
		logged = false;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
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
		return admin;
	}

	public void setAdmin(boolean isAdmin) {
		this.admin = isAdmin;
	}
}
