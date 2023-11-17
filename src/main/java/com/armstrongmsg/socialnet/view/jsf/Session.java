package com.armstrongmsg.socialnet.view.jsf;

import com.armstrongmsg.socialnet.model.authentication.UserToken;

public class Session {
	private boolean admin;
	private boolean logged;
	private UserToken userToken;

	public Session(UserToken userToken) {
		this.userToken = userToken;
		this.admin = false;
		this.logged = false;
	}

	public UserToken getUserToken() {
		return userToken;
	}

	public void setUser(UserToken userToken) {
		this.userToken = userToken;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
}
