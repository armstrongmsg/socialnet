package com.armstrongmsg.socialnet.model.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public interface AuthenticationPlugin {
	void setUp(Admin admin);
	UserToken authenticate(Map<String, String> credentials) throws AuthenticationException;
	User getUser(UserToken token) throws AuthenticationException;
}
