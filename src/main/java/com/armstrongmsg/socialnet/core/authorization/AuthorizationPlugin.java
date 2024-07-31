package com.armstrongmsg.socialnet.core.authorization;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.User;

public interface AuthorizationPlugin {
	void setUp(Object ... params);
	void authorize(User requester, Operation operation) throws UnauthorizedOperationException;
}
