package com.armstrongmsg.socialnet.view.jsf;

import java.util.Objects;

import com.armstrongmsg.socialnet.view.jsf.model.UserView;

public class Session {
	private boolean admin;
	private boolean logged;
	private String userToken;
	private UserView viewUser;

	public Session(String userToken) {
		this.userToken = userToken;
		this.admin = false;
		this.logged = false;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUser(String userToken) {
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

	public UserView getCurrentViewUser() {
		return viewUser;
	}

	public void setCurrentViewUser(UserView viewUser) {
		this.viewUser = viewUser;
	}

	@Override
	public int hashCode() {
		return Objects.hash(admin, logged, userToken, viewUser);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		return admin == other.admin && logged == other.logged && Objects.equals(userToken, other.userToken)
				&& Objects.equals(viewUser, other.viewUser);
	}
}
