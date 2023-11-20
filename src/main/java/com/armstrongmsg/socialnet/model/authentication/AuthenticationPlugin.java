package com.armstrongmsg.socialnet.model.authentication;

import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public interface AuthenticationPlugin {
	void setUp(Admin admin, List<User> users);
	UserToken authenticate(Map<String, String> credentials);
}
