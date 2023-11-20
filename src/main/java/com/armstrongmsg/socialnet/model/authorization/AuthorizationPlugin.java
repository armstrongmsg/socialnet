package com.armstrongmsg.socialnet.model.authorization;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public interface AuthorizationPlugin {
	void setUp(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships, List<Follow> follows);
	void authorize(User requester, Operation operation) throws UnauthorizedOperationException;
}
