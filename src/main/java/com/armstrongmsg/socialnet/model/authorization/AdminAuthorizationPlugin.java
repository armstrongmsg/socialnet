package com.armstrongmsg.socialnet.model.authorization;

import java.util.Arrays;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public class AdminAuthorizationPlugin implements AuthorizationPlugin {
	private Admin admin;
	private static final List<OperationType> ADMIN_ONLY_OPERATIONS = Arrays.asList();
	
	public AdminAuthorizationPlugin() {
	}
	
	@Override
	public void authorize(User requester, Operation operation) throws UnauthorizedOperationException {
		if (ADMIN_ONLY_OPERATIONS.contains(operation.getOperation())) {
			if (!admin.equals(requester)) {
				// FIXME constant
				throw new UnauthorizedOperationException(
						String.format("User {} is not authorized to perform operation {}.", requester.getUserId(), 
								operation.getOperation().getValue()));
			}
		}
	}

	@Override
	public void setUp(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships,
			List<Follow> follows) {
		this.admin = admin;
	}	
}
