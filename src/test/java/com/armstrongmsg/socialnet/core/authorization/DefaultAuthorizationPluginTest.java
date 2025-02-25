package com.armstrongmsg.socialnet.core.authorization;

import org.junit.Assert;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.StorageFacade;

public class DefaultAuthorizationPluginTest {
	private static final String USER_ID = "userId";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ADMIN_ID = "adminId";
	private static final String ADMIN_PASSWORD = "adminPassword";
	private DefaultAuthorizationPlugin plugin;
	private StorageFacade storageFacade;
	private Operation operation;
	private User user;
	private Admin admin;

	@Test
	public void testAuthorize() throws UnauthorizedOperationException {
		user = new User(USER_ID, USERNAME, PASSWORD, null);
		admin = new Admin(ADMIN_ID, ADMIN_ID, ADMIN_PASSWORD);
		plugin = new DefaultAuthorizationPlugin(storageFacade);
		plugin.setUp(admin);
		
		for (OperationType type : OperationType.values()) {
			if (type == OperationType.GET_USER_POSTS_ADMIN) {
				operation = new OperationOnUser(OperationType.GET_USER_POSTS_ADMIN, user);
			} else {
				operation = new Operation(type);
			}
			
			if (DefaultAuthorizationPlugin.ADMIN_ONLY_OPERATIONS.contains(type)) {
				try {
					plugin.authorize(user, operation);
					Assert.fail("Expected UnauthorizedOperationException.");
				} catch (UnauthorizedOperationException e) {

				}
			} else {
				plugin.authorize(user, operation);
			}
			
			plugin.authorize(admin, operation);
		}
	}
}
