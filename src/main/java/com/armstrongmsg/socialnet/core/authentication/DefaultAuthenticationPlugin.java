package com.armstrongmsg.socialnet.core.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.StorageFacade;

public class DefaultAuthenticationPlugin implements AuthenticationPlugin {
	private static final String TOKEN_FIELD_SEPARATOR = "#&#";
	private Admin admin;
	private StorageFacade storageFacade;
	
	public DefaultAuthenticationPlugin(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}

	@Override
	public String authenticate(Map<String, String> credentials) throws AuthenticationException, InternalErrorException {
		String username = credentials.get(AuthenticationParameters.USERNAME_KEY);
		String password = credentials.get(AuthenticationParameters.PASSWORD_KEY);
		return authenticate(username, password);
	}
	
	private String authenticate(String username, String password) throws AuthenticationException, InternalErrorException {
		User user = null;
		
		if (admin.getUsername().equals(username)) {
			user = admin;
		} else {
			try {
				user = this.storageFacade.getUserByUsername(username);
			} catch (UserNotFoundException e) {
				throw new AuthenticationException(String.format(Messages.Exception.COULD_NOT_FIND_USER, username));
			}
		}

		if (user != null && user.getPassword().equals(password)) {
			return user.getUserId() + TOKEN_FIELD_SEPARATOR + user.getUsername();
		}
		
		throw new AuthenticationException(String.format(Messages.Exception.INVALID_CREDENTIALS, username));
	}

	@Override
	public void setUp(Object ... objects) {
		this.admin = (Admin) objects[0];
	}

	@Override
	public User getUser(String token) throws AuthenticationException {
		String[] tokenFields = token.split(TOKEN_FIELD_SEPARATOR);
		String username = tokenFields[1];
		
		try {
			if (admin.getUsername().equals(username)) {
				return admin;
			} else {
				return this.storageFacade.getUserByUsername(username);
			}
		} catch (UserNotFoundException e) {
			throw new AuthenticationException(Messages.Exception.USER_NOT_FOUND_EXCEPTION);
		} catch (InternalErrorException e) {
			throw new AuthenticationException();
		}
	}
}
