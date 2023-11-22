package socialnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.PropertiesNames;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.storage.StorageManager;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

import socialnet.stub.StubAuthenticationPlugin;
import socialnet.stub.StubAuthorizationPlugin;

public class IntegrationTest {
	private static final String ADMIN_USERNAME = "admin-username";
	private static final String ADMIN_PASSWORD = "admin-password";
	private static final String NEW_USERNAME_1 = "new-username-1";
	private static final String NEW_USER_PASSWORD_1 = "new-user-password-1";
	private static final String NEW_USER_PROFILE_DESCRIPTION_1 = "new-user-profile-description-1";
	private static final String NOT_ADMIN_ID = "not-admin-id";
	private static final String NOT_ADMIN_USERNAME = "not-admin-username";
	private ApplicationFacade facade;
	private StorageManager storageManager;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	
	@Before
	public void setUp() throws FatalErrorException {
		ApplicationFacade.reset();
		
		storageManager = Mockito.mock(StorageManager.class);
		Mockito.when(storageManager.readUsers()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readGroups()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFriendships()).thenReturn(Arrays.asList());
		Mockito.when(storageManager.readFollows()).thenReturn(Arrays.asList());
		
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_USERNAME)).
			thenReturn(ADMIN_USERNAME);
		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_PASSWORD)).
			thenReturn(ADMIN_PASSWORD);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHENTICATION_PLUGIN_CLASS_NAME)).
			thenReturn(StubAuthenticationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME)).
			thenReturn(StubAuthorizationPlugin.class.getCanonicalName());
		
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		facade = ApplicationFacade.getInstance(storageManager);
	}
	
	@Test
	public void testAdminLogin() throws AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		UserToken adminToken = facade.login(adminCredentials);
		
		assertEquals(ADMIN_USERNAME, adminToken.getUsername());
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAdminLoginWithIncorrectCredentials() throws AuthenticationException {
		Map<String, String> adminIncorrectCredentials = new HashMap<String, String>();
		adminIncorrectCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminIncorrectCredentials.put(AuthenticationParameters.PASSWORD_KEY, "incorrect password");
		
		facade.login(adminIncorrectCredentials);
	}
	
	@Test
	public void testGetCreateRemoveUserByAdmin() throws AuthenticationException, UnauthorizedOperationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		UserToken adminToken = facade.login(adminCredentials);
		
		List<User> users = facade.getUsers(adminToken);
		
		assertTrue(users.isEmpty());
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertEquals(1, usersAfterCreation.size());
		assertEquals(NEW_USERNAME_1, usersAfterCreation.get(0).getUsername());
		assertEquals(NEW_USER_PASSWORD_1, usersAfterCreation.get(0).getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, usersAfterCreation.get(0).getProfile().getDescription());
		assertTrue(usersAfterCreation.get(0).getProfile().getPosts().isEmpty());
		
		facade.removeUser(adminToken, usersAfterCreation.get(0).getUserId());
		
		List<User> usersAfterRemoval = facade.getUsers(adminToken);
		
		assertTrue(usersAfterRemoval.isEmpty());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotGetAllUsersWithNonAdminToken() throws UnauthorizedOperationException {
		UserToken nonAdminToken = new UserToken(NOT_ADMIN_ID, NOT_ADMIN_USERNAME, "");
		
		facade.getUsers(nonAdminToken);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotAddUserAsAdminWithNonAdminToken() throws UnauthorizedOperationException {
		UserToken nonAdminToken = new UserToken(NOT_ADMIN_ID, NOT_ADMIN_USERNAME, "");
		
		facade.addUser(nonAdminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotRemoveUser() throws AuthenticationException, UnauthorizedOperationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		UserToken adminToken = facade.login(adminCredentials);
		UserToken nonAdminToken = new UserToken(NOT_ADMIN_ID, NOT_ADMIN_USERNAME, "");
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		facade.removeUser(nonAdminToken, usersAfterCreation.get(0).getUserId());
	}
	
	@Test
	public void testGetAndCreateUserBySelf() throws UnauthorizedOperationException, AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		UserToken adminToken = facade.login(adminCredentials);
		
		List<User> users = facade.getUsers(adminToken);
		
		assertTrue(users.isEmpty());
		
		facade.addUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertEquals(1, usersAfterCreation.size());
		assertEquals(NEW_USERNAME_1, usersAfterCreation.get(0).getUsername());
		assertEquals(NEW_USER_PASSWORD_1, usersAfterCreation.get(0).getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, usersAfterCreation.get(0).getProfile().getDescription());
		assertTrue(usersAfterCreation.get(0).getProfile().getPosts().isEmpty());
	}
	
	@After
	public void TearDown() {
		propertiesUtilMock.close();
	}
}
