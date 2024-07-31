package com.armstrongmsg.socialnet.core.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
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
			try {
				user = this.storageFacade.getUserByUsername(userId);
			} catch (UserNotFoundException e) {
				throw new AuthenticationException(String.format(Messages.Exception.COULD_NOT_FIND_USER, userId));
			}
		}

		if (user != null && user.getPassword().equals(password)) {
			return new UserToken(user.getUserId(), user.getUsername(), user.getProfile().getDescription());
		}
		
		throw new AuthenticationException(String.format(Messages.Exception.INVALID_CREDENTIALS, userId));
	}

	@Override
	public void setUp(Object ... objects) {
		this.admin = (Admin) objects[0];
	}

	@Override
	public User getUser(UserToken token) throws AuthenticationException {
		try {
			if (admin.getUsername().equals(token.getUserId())) {
				return admin;
			} else {
				return this.storageFacade.getUserById(token.getUserId());
			}
		// TODO test
		} catch (UserNotFoundException e) {
			throw new AuthenticationException(Messages.Exception.USER_NOT_FOUND_EXCEPTION);
		}
	}
}
