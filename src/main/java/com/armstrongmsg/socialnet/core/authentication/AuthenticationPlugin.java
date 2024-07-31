package com.armstrongmsg.socialnet.core.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public interface AuthenticationPlugin {
	// TODO should be more flexible on the parameters E.g.: setUp(Object...params)
	void setUp(Admin admin);
	UserToken authenticate(Map<String, String> credentials) throws AuthenticationException;
	User getUser(UserToken token) throws AuthenticationException;
}
