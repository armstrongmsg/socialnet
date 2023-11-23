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
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.authentication.DefaultAuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.model.authorization.DefaultAuthorizationPlugin;
import com.armstrongmsg.socialnet.storage.StorageManager;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class IntegrationTest {
	private static final String ADMIN_USERNAME = "admin-username";
	private static final String ADMIN_PASSWORD = "admin-password";
	private static final String NEW_USERNAME_1 = "new-username-1";
	private static final String NEW_USER_PASSWORD_1 = "new-user-password-1";
	private static final String NEW_USER_PROFILE_DESCRIPTION_1 = "new-user-profile-description-1";
	private static final String NEW_POST_TITLE = "new-post-title";
	private static final String NEW_POST_CONTENT = "new-post-content";
	private static final PostVisibility NEW_POST_VISIBILITY = PostVisibility.PUBLIC;
	private static final String NEW_USERNAME_2 = "new-username-2";
	private static final String NEW_USER_PASSWORD_2 = "new-user-password-2";
	private static final String NEW_USER_PROFILE_DESCRIPTION_2 = "new-user-profile-description-2";
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
			thenReturn(DefaultAuthenticationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME)).
			thenReturn(DefaultAuthorizationPlugin.class.getCanonicalName());
		
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
		UserToken adminToken = loginAsAdmin();
		
		List<User> users = facade.getUsers(adminToken);
		
		assertTrue(users.isEmpty());
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertEquals(2, usersAfterCreation.size());
		
		User createdUser1 = usersAfterCreation.get(0);
		assertEquals(NEW_USERNAME_1, createdUser1.getUsername());
		assertEquals(NEW_USER_PASSWORD_1, createdUser1.getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, createdUser1.getProfile().getDescription());
		assertTrue(createdUser1.getProfile().getPosts().isEmpty());
		User createdUser2 = usersAfterCreation.get(1);
		assertEquals(NEW_USERNAME_2, createdUser2.getUsername());
		assertEquals(NEW_USER_PASSWORD_2, createdUser2.getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_2, createdUser2.getProfile().getDescription());
		assertTrue(createdUser2.getProfile().getPosts().isEmpty());
		
		facade.removeUser(adminToken, usersAfterCreation.get(0).getUserId());
		
		List<User> usersAfterRemoval = facade.getUsers(adminToken);
		
		assertEquals(1, usersAfterRemoval.size());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotGetAllUsersWithNonAdminToken() throws UnauthorizedOperationException, AuthenticationException {	
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getUsers(userToken);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotAddUserAsAdminWithNonAdminToken() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.addUser(userToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotRemoveUser() throws AuthenticationException, UnauthorizedOperationException {		
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		facade.removeUser(userToken, usersAfterCreation.get(1).getUserId());
	}
	
	@Test
	public void testGetAndCreateUserBySelf() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		List<User> users = facade.getUsers(adminToken);
		
		assertTrue(users.isEmpty());
		
		facade.addUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertEquals(1, usersAfterCreation.size());
		
		User createdUser = usersAfterCreation.get(0);
		assertEquals(NEW_USERNAME_1, createdUser.getUsername());
		assertEquals(NEW_USER_PASSWORD_1, createdUser.getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, createdUser.getProfile().getDescription());
		assertTrue(createdUser.getProfile().getPosts().isEmpty());
	}
	
	@Test
	public void testCreateAndGetPost() throws AuthenticationException, UnauthorizedOperationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<Post> userPosts = facade.getUserPosts(userToken, userToken.getUserId());
		assertTrue(userPosts.isEmpty());
		
		facade.createPost(userToken, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY);
		
		List<Post> userPostsAfterCreation = facade.getUserPosts(userToken, userToken.getUserId());
		List<Post> userPostsAfterCreationAdminVersion = facade.getUserPosts(adminToken, userToken.getUserId());
		
		assertEquals(1, userPostsAfterCreation.size());
		Post createdPost = userPostsAfterCreation.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
		
		assertEquals(1, userPostsAfterCreationAdminVersion.size());
		createdPost = userPostsAfterCreationAdminVersion.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
	}
	
	@Test(expected = AuthenticationException.class)
	public void testNonAdminUserCannotGetPostsFromOtherUser() throws AuthenticationException, UnauthorizedOperationException { 
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getUserPosts(userToken, NEW_USERNAME_2);
	}
	
	private UserToken loginAsAdmin() throws AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		return facade.login(adminCredentials);
	}
	
	private UserToken loginAsUser(String username, String password) throws AuthenticationException {
		Map<String, String> userCredentials = new HashMap<String, String>();
		userCredentials.put(AuthenticationParameters.USERNAME_KEY, username);
		userCredentials.put(AuthenticationParameters.PASSWORD_KEY, password);
		
		return facade.login(userCredentials);
	}
	
	@After
	public void TearDown() {
		propertiesUtilMock.close();
	}
}
