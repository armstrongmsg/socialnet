package com.armstrongmsg.socialnet.model.authorization;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public interface AuthorizationPlugin {
	void setUp(Admin admin);
	void authorize(User requester, Operation operation) throws UnauthorizedOperationException;
}
