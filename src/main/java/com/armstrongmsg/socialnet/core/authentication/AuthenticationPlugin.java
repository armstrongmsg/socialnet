package com.armstrongmsg.socialnet.core.authentication;

import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.model.User;

public interface AuthenticationPlugin {
	void setUp(Object ... params);
	String authenticate(Map<String, String> credentials) throws AuthenticationException;
	User getUser(String token) throws AuthenticationException;
}
