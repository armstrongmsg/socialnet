package com.armstrongmsg.socialnet.view.jsf;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(admin, logged, userToken);
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
		return admin == other.admin && logged == other.logged && Objects.equals(userToken, other.userToken);
	}
}
