package com.armstrongmsg.socialnet.model.authentication;

import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public class LocalPasswordBasedAuthenticationPlugin implements AuthenticationPlugin {
	private List<User> users;
	private Admin admin;
	
	public LocalPasswordBasedAuthenticationPlugin() {
	}

	@Override
	public UserToken authenticate(Map<String, String> credentials) throws AuthenticationException {
		// FIXME constant
		String userId = credentials.get("USER_ID");
		// FIXME constant
		String password = credentials.get("PASSWORD");
		return authenticate(userId, password);
	}
	
	private UserToken authenticate(String userId, String password) throws AuthenticationException {
		User user = findUserByUsername(userId);
		
		if (user.getPassword().equals(password)) {
			return new UserToken(user.getUserId(), user.getUsername(), user.getProfile().getDescription());
		}
		
		// TODO add message
		throw new AuthenticationException();
	}

	private User findUserByUsername(String username) throws AuthenticationException {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		if (admin.getUsername().equals(username)) {
			return admin;
		}

		// TODO add message
		throw new AuthenticationException();
	}

	@Override
	public void setUp(Admin admin, List<User> users) {
		this.admin = admin;
		this.users = users;
	}
}
