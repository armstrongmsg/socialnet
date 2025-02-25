package com.armstrongmsg.socialnet.core.authorization;

import java.util.Arrays;
import java.util.List;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.StorageFacade;

public class DefaultAuthorizationPlugin implements AuthorizationPlugin {
	private Admin admin;
	static final List<OperationType> ADMIN_ONLY_OPERATIONS = Arrays.asList(OperationType.GET_ALL_USERS,
			OperationType.REMOVE_USER, OperationType.ADD_USER, OperationType.GET_USER_POSTS_ADMIN, OperationType.ADD_FRIENDSHIP_ADMIN, 
			OperationType.GET_FRIENDS_ADMIN, OperationType.ADD_FOLLOW_ADMIN, OperationType.GET_FOLLOWED_USERS_ADMIN);
	
	public DefaultAuthorizationPlugin(StorageFacade storageFacade) {
	}
	
	@Override
	public void authorize(User requester, Operation operation) throws UnauthorizedOperationException {
		if (operation.getOperation().equals(OperationType.GET_USER_POSTS_ADMIN)) {
			OperationOnUser userOperation = (OperationOnUser) operation;
			
			if (!admin.equals(requester) &&
					!userOperation.getTarget().equals(requester)) {
				throw new UnauthorizedOperationException(
						String.format(Messages.Exception.USER_IS_NOT_AUTHORIZED, requester.getUserId(), 
								operation.getOperation().getValue()));
			}
		}
		
		if (ADMIN_ONLY_OPERATIONS.contains(operation.getOperation())) {
			if (!admin.equals(requester)) {
				throw new UnauthorizedOperationException(
						String.format(Messages.Exception.USER_IS_NOT_AUTHORIZED, requester.getUserId(), 
								operation.getOperation().getValue()));
			}
		}
	}

	@Override
	public void setUp(Object ... objects) {
		this.admin = (Admin) objects[0];
	}	
}
