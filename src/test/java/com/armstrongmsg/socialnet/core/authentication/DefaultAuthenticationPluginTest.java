package com.armstrongmsg.socialnet.core.authentication;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.StorageFacade;

public class DefaultAuthenticationPluginTest {
	private static final String USER_ID = "USER_ID";
	private static final String USER_NAME = "USERNAME";
	private static final String PASSWORD = "PASSWORD";
	private static final String ADMIN_ID = "ADMIN_ID";
	private static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";
	private StorageFacade storageFacade;
	private Map<String, String> credentials;
	private User authenticationUser;
	private User admin;
	private HashMap<String, String> adminCredentials;

	@Before
	public void setUp() {
		admin = new Admin(ADMIN_ID, ADMIN_ID, ADMIN_PASSWORD);
		
		credentials = new HashMap<String, String>();
		credentials.put(AuthenticationParameters.USERNAME_KEY, USER_NAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_ID);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		authenticationUser = new User(USER_ID, USER_NAME, PASSWORD, null);
	}
	
	@Test
	public void testRegularUserAuthentication() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		String token = plugin.authenticate(credentials);
		
		User user = plugin.getUser(token);
		
		assertEquals(authenticationUser, user);
	}
	
	@Test
	public void testAdminAuthentication() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		String token = plugin.authenticate(adminCredentials);
		User user = plugin.getUser(token);
		
		assertEquals(admin, user);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAuthenticateFailsWithWrongUserCredentials() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		credentials = new HashMap<String, String>();
		credentials.put(AuthenticationParameters.USERNAME_KEY, USER_NAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, "wrongPassword");
		
		plugin.authenticate(credentials);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAuthenticateFailsWithWrongAdminCredentials() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		credentials = new HashMap<String, String>();
		credentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_ID);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, "wrongPassword");
		
		plugin.authenticate(credentials);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAuthenticateFailsWithInvalidUser() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		Mockito.when(storageFacade.getUserByUsername("invalid-username")).thenThrow(UserNotFoundException.class);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		credentials = new HashMap<String, String>();
		credentials.put(AuthenticationParameters.USERNAME_KEY, "invalid-username");
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		plugin.authenticate(credentials);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testGetUserFailsWithInvalidToken() throws UserNotFoundException, InternalErrorException, AuthenticationException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		plugin.getUser("invalidToken");
	}
	
	@Test(expected = AuthenticationException.class)
	public void testGetUserFailsWithInvalidUsername() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		Mockito.when(storageFacade.getUserByUsername("invalid-username")).thenThrow(UserNotFoundException.class);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		String token = USER_ID + DefaultAuthenticationPlugin.TOKEN_FIELD_SEPARATOR + "invalid-username";
		
		plugin.getUser(token);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testGetUserFailsIfUserRetrievalFails() 
			throws AuthenticationException, InternalErrorException, UserNotFoundException {
		storageFacade = Mockito.mock(StorageFacade.class);
		Mockito.when(storageFacade.getUserByUsername(USER_NAME)).thenReturn(authenticationUser);
		Mockito.when(storageFacade.getUserByUsername("invalid-username")).thenThrow(InternalErrorException.class);
		
		DefaultAuthenticationPlugin plugin = new DefaultAuthenticationPlugin(storageFacade);
		plugin.setUp(admin);
		
		String token = USER_ID + DefaultAuthenticationPlugin.TOKEN_FIELD_SEPARATOR + "invalid-username";
		
		plugin.getUser(token);
	}
}
