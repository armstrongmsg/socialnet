package socialnet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.model.authentication.DefaultAuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.model.authorization.DefaultAuthorizationPlugin;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.storage.cache.DefaultCache;
import com.armstrongmsg.socialnet.storage.database.DatabaseManager;
import com.armstrongmsg.socialnet.storage.database.DefaultDatabaseManager;
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
	private static final PostVisibility NEW_POST_VISIBILITY_2 = PostVisibility.PRIVATE;
	private static final String NEW_POST_TITLE_2 = "new-post-title-2";
	private static final String NEW_POST_CONTENT_2 = "new-post-content-2";
	private ApplicationFacade facade;
	private StorageFacade storageFacade;
	private DefaultCache cache;
	private DatabaseManager databaseManager;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	
	@Before
	public void setUp() throws FatalErrorException {
		ApplicationFacade.reset();
		
		this.cache = new DefaultCache();
		this.databaseManager = new DefaultDatabaseManager();
				
		storageFacade = new StorageFacade(this.cache, this.databaseManager);
		
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_USERNAME)).
			thenReturn(ADMIN_USERNAME);
		Mockito.when(propertiesUtil.getProperty(PropertiesNames.ADMIN_PASSWORD)).
			thenReturn(ADMIN_PASSWORD);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHENTICATION_PLUGIN_CLASS_NAME)).
			thenReturn(DefaultAuthenticationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME)).
			thenReturn(DefaultAuthorizationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty("BOOTSTRAP")).
			thenReturn("false");
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		facade = ApplicationFacade.getInstance(storageFacade);
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
	public void testIsAdmin() throws AuthenticationException, UnauthorizedOperationException {
		UserToken adminToken = loginAsAdmin();
		
		assertTrue(facade.userIsAdmin(adminToken.getUserId()));
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertFalse(facade.userIsAdmin(usersAfterCreation.get(0).getUserId()));
		assertFalse(facade.userIsAdmin(usersAfterCreation.get(1).getUserId()));
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
	public void testCreateAndGetPostByAdmin() throws AuthenticationException, UnauthorizedOperationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<Post> userPosts = facade.getUserPosts(adminToken, userToken.getUserId());
		assertTrue(userPosts.isEmpty());
		
		facade.createPost(userToken, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY);
		
		List<Post> userPostsAfterCreation = facade.getUserPosts(adminToken, userToken.getUserId());
		
		assertEquals(1, userPostsAfterCreation.size());
		Post createdPost = userPostsAfterCreation.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
	}

	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminUserCannotGetPostsByUserId() throws AuthenticationException, UnauthorizedOperationException { 
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getUserPosts(userToken, userToken.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminUserCannotGetPostsFromOtherUser() throws AuthenticationException, UnauthorizedOperationException { 
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		UserToken userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.getUserPosts(userToken1, userToken2.getUserId());
	}
	
	@Test
	public void testCreateAndGetPostByNonAdmin() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<Post> userPosts = facade.getSelfPosts(userToken);
		assertTrue(userPosts.isEmpty());
		
		facade.createPost(userToken, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY);
		
		List<Post> userPostsAfterCreation = facade.getSelfPosts(userToken);
		
		assertEquals(1, userPostsAfterCreation.size());
		Post createdPost = userPostsAfterCreation.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
	}
	
	@Test
	public void testAddFriendshipAdmin() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<User> user1Friends = facade.getFriends(adminToken, user1.getUserId());
		List<User> user2Friends = facade.getFriends(adminToken, user2.getUserId());
		
		assertTrue(user1Friends.contains(user2));
		assertTrue(user2Friends.contains(user1));
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotAddFriendshipByUserId() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		facade.addFriendshipAdmin(userToken, user1.getUserId(), user2.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotGetFriendsById() throws AuthenticationException, UnauthorizedOperationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getFriends(userToken, userToken.getUserId());
	}
	
	@Test
	public void testAddFriendship() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		UserToken userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFriendship(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> friends1 = facade.getSelfFriends(userToken1);
		List<UserSummary> friends2 = facade.getSelfFriends(userToken2);
		
		UserSummary user1Summary = new UserSummary(NEW_USERNAME_1, NEW_USER_PROFILE_DESCRIPTION_1);
		UserSummary user2Summary = new UserSummary(NEW_USERNAME_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		assertTrue(friends1.contains(user2Summary));
		assertTrue(friends2.contains(user1Summary));
	}
	
	@Test
	public void testGetFriendsPosts() throws AuthenticationException, UnauthorizedOperationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		UserToken userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFriendship(userToken1, NEW_USERNAME_2);
		
		facade.createPost(userToken1, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY);
		facade.createPost(userToken1, NEW_POST_TITLE_2, NEW_POST_CONTENT_2, NEW_POST_VISIBILITY_2);
		
		List<Post> postsFriendsUser1 = facade.getFriendsPosts(userToken1);
		List<Post> postsFriendsUser2 = facade.getFriendsPosts(userToken2);
		
		assertTrue(postsFriendsUser1.isEmpty());
		assertEquals(2, postsFriendsUser2.size());
		Post post1 = postsFriendsUser2.get(0);
		Post post2 = postsFriendsUser2.get(1);
		
		assertEquals(NEW_POST_TITLE, post1.getTitle());
		assertEquals(NEW_POST_CONTENT, post1.getContent());
		assertEquals(NEW_POST_VISIBILITY, post1.getVisibility());
		
		assertEquals(NEW_POST_TITLE_2, post2.getTitle());
		assertEquals(NEW_POST_CONTENT_2, post2.getContent());
		assertEquals(NEW_POST_VISIBILITY_2, post2.getVisibility());
	}
	
	@Test
	public void testAddFollowAdmin() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<User> user1Follows = facade.getFollowedUsers(adminToken, user1.getUserId());
		List<User> user2Follows = facade.getFollowedUsers(adminToken, user2.getUserId());
	
		assertEquals(1, user1Follows.size());
		assertTrue(user1Follows.contains(user2));
		
		assertTrue(user2Follows.isEmpty());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotFollowById() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		UserToken userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		facade.addFollowAdmin(userToken, user1.getUserId(), user2.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotGetFollowsById() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getFollowedUsers(userToken1, user1.getUserId());
	}
	
	@Test
	public void testAddFollow() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		UserToken userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFollow(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> followsUser1 = facade.getFollowedUsers(userToken1);
		List<UserSummary> followsUser2 = facade.getFollowedUsers(userToken2);
		
		UserSummary user2Summary = new UserSummary(NEW_USERNAME_2, NEW_USER_PROFILE_DESCRIPTION_2);
				
		assertEquals(1, followsUser1.size());
		assertTrue(followsUser1.contains(user2Summary));
		
		assertTrue(followsUser2.isEmpty());
	}
	
	@Test
	public void testGetUserSummaries() throws UnauthorizedOperationException, AuthenticationException {
		UserToken adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		UserToken userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		UserToken userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		List<UserSummary> summariesUser1 = facade.getUserSummaries(userToken1);
		List<UserSummary> summariesUser2 = facade.getUserSummaries(userToken2);
		
		assertEquals(1, summariesUser1.size());
		assertEquals(NEW_USERNAME_2, summariesUser1.get(0).getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_2, summariesUser1.get(0).getProfileDescription());
		
		assertEquals(1, summariesUser2.size());
		assertEquals(NEW_USERNAME_1, summariesUser2.get(0).getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, summariesUser2.get(0).getProfileDescription());
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
