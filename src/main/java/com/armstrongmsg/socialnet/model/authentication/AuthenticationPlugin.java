package com.armstrongmsg.socialnet.model.authentication;

import java.util.Map;

public interface AuthenticationPlugin {
	UserToken authenticate(Map<String, String> credentials);
}
