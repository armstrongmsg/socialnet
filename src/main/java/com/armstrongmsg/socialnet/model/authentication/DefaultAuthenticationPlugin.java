package com.armstrongmsg.socialnet.model.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.StorageFacade;

public class DefaultAuthenticationPlugin implements AuthenticationPlugin {
	private Admin admin;
	
	private StorageFacade storageFacade;
	
	public DefaultAuthenticationPlugin(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

	@Override
	public UserToken authenticate(Map<String, String> credentials) throws AuthenticationException {
		String userId = credentials.get(AuthenticationParameters.USERNAME_KEY);
		String password = credentials.get(AuthenticationParameters.PASSWORD_KEY);
		return authenticate(userId, password);
	}
	
	private UserToken authenticate(String userId, String password) throws AuthenticationException {
		User user = null;
		
		if (admin.getUsername().equals(userId)) {
			user = admin;
		} else {
			user = this.storageFacade.getUserByUsername(userId);
		}

		if (user != null && user.getPassword().equals(password)) {
			return new UserToken(user.getUserId(), user.getUsername(), user.getProfile().getDescription());
		}
		
		// TODO add message
		throw new AuthenticationException();
	}

	@Override
	public void setUp(Admin admin) {
		this.admin = admin;
	}
}
