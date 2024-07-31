package com.armstrongmsg.socialnet.core.authorization;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;

public interface AuthorizationPlugin {
	// TODO should be more flexible on the parameters E.g.: setUp(Object...params)
	void setUp(Admin admin);
	void authorize(User requester, Operation operation) throws UnauthorizedOperationException;
}
