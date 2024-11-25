package com.armstrongmsg.socialnet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.ConfigurationPropertiesDefaults;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.DefaultAuthenticationPlugin;
import com.armstrongmsg.socialnet.core.authorization.DefaultAuthorizationPlugin;
import com.armstrongmsg.socialnet.core.feed.DefaultFeedPolicy;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.storage.cache.Cache;
import com.armstrongmsg.socialnet.storage.cache.LruCache;
import com.armstrongmsg.socialnet.storage.cache.NoOperationCache;
import com.armstrongmsg.socialnet.storage.database.DatabaseManager;
import com.armstrongmsg.socialnet.storage.database.PictureLoadingAwareDatabaseManager;
import com.armstrongmsg.socialnet.util.ApplicationPaths;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PersistenceTest;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

@RunWith(Parameterized.class)
public class IntegrationTest extends PersistenceTest {
	private static final String TEST_CACHE_PATH = TEST_DIRECTORY + File.separator + "cache";
	private static final String ADMIN_USERNAME = "admin-username";
	private static final String ADMIN_PASSWORD = "admin-password";
	private static final String NEW_USERNAME_1 = "new-username-1";
	private static final String NEW_USER_PASSWORD_1 = "new-user-password-1";
	private static final String NEW_USER_PROFILE_DESCRIPTION_1 = "new-user-profile-description-1";
	private static final String UPDATED_USER_PROFILE_DESCRIPTION_1 = "updated-profile-description-1";
	private static final String NEW_POST_TITLE = "new-post-title";
	private static final String NEW_POST_CONTENT = "new-post-content";
	private static final PostVisibility NEW_POST_VISIBILITY = PostVisibility.PUBLIC;
	private static final String NEW_USERNAME_2 = "new-username-2";
	private static final String NEW_USER_PASSWORD_2 = "new-user-password-2";
	private static final String NEW_USER_PROFILE_DESCRIPTION_2 = "new-user-profile-description-2";
	private static final PostVisibility NEW_POST_VISIBILITY_2 = PostVisibility.PRIVATE;
	private static final String NEW_POST_TITLE_2 = "new-post-title-2";
	private static final String NEW_POST_CONTENT_2 = "new-post-content-2";
	private static final String NEW_POST_TITLE_3 = "new-post-title-3";
	private static final String NEW_POST_CONTENT_3 = "new-post-content-3";
	private static final String NEW_POST_TITLE_4 = "new-post-title-4";
	private static final String NEW_POST_CONTENT_4 = "new-post-content-4";
	private static final String MAX_NUMBER_OF_POSTS = "2";
	private static final byte[] PICTURE_DATA = new byte[] {1, 2, 3};
	private static final byte[] PICTURE_DATA_2 = new byte[] {4, 5, 6};
	private static final byte[] PICTURE_DATA_3 = new byte[] {7, 8, 9};
	private static final byte[] PICTURE_DATA_4 = new byte[] {10, 11, 12};
	private ApplicationFacade facade;
	private StorageFacade storageFacade;
	private Cache cache;
	private DatabaseManager databaseManager;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private MockedStatic<ApplicationPaths> pathsUtilMock;
	private String cacheType;
	private String databaseManagerType;
	
	@Parameterized.Parameters
	public static List<Object[]> getTestParameters() {
		List<Object[]> args = new ArrayList<Object[]>();
		args.add(new Object[] {NoOperationCache.class.getCanonicalName(), 
				PictureLoadingAwareDatabaseManager.class.getCanonicalName()});
		args.add(new Object[] {LruCache.class.getCanonicalName(), 
				PictureLoadingAwareDatabaseManager.class.getCanonicalName()});
		return args;
	}
	
	public IntegrationTest(String cacheType, String databaseManagerType) throws FatalErrorException {
		this.cacheType = cacheType;
		this.databaseManagerType = databaseManagerType;
	}
	
	@Before
	@Override
	public void setUp() throws FatalErrorException {
		super.setUp();
		
		pathsUtilMock = Mockito.mockStatic(ApplicationPaths.class);
		
		Mockito.when(ApplicationPaths.getApplicationBasePath()).
			thenReturn(TEST_CACHE_PATH);
		
		ApplicationFacade.reset();
		
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.ADMIN_USERNAME)).
			thenReturn(ADMIN_USERNAME);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.ADMIN_PASSWORD)).
			thenReturn(ADMIN_PASSWORD);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHENTICATION_PLUGIN_CLASS_NAME)).
			thenReturn(DefaultAuthenticationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME)).
			thenReturn(DefaultAuthorizationPlugin.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.FEED_POLICY_CLASS_NAME)).
			thenReturn(DefaultFeedPolicy.class.getCanonicalName());
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MAX_NUMBER_OF_POSTS)).
			thenReturn(MAX_NUMBER_OF_POSTS);
		Mockito.when(propertiesUtil.getProperty("BOOTSTRAP")).
			thenReturn("false");
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_PERSISTENCE_UNIT, 
			ConfigurationPropertiesDefaults.DATABASE_PERSISTENCE_UNIT)).thenReturn("default-test");
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE)).
			thenReturn("false");
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.PICTURE_REPOSITORY_LOCAL_PATH)).
			thenReturn(PersistenceTest.TEST_DIRECTORY);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.CACHE_MAX_CAPACITY)).
			thenReturn("100");
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		this.cache = (Cache) new ClassFactory().createInstance(cacheType);
		this.databaseManager = (DatabaseManager) new ClassFactory().createInstance(databaseManagerType);
		
		storageFacade = new StorageFacade(this.cache, this.databaseManager);
		
		facade = ApplicationFacade.getInstance(storageFacade);
	}
	
	@Test
	public void testAdminLogin() throws AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		String adminToken = facade.login(adminCredentials);
		
		assertNotNull(adminToken);
		assertFalse(adminToken.isBlank());
	}
	
	@Test(expected = AuthenticationException.class)
	public void testAdminLoginWithIncorrectCredentials() throws AuthenticationException {
		Map<String, String> adminIncorrectCredentials = new HashMap<String, String>();
		adminIncorrectCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminIncorrectCredentials.put(AuthenticationParameters.PASSWORD_KEY, "incorrect password");
		
		facade.login(adminIncorrectCredentials);
	}
	
	@Test(expected = AuthenticationException.class)
	public void testLoginAsInvalidUser() throws AuthenticationException {
		Map<String, String> invalidUserCredentials = new HashMap<String, String>();
		invalidUserCredentials.put(AuthenticationParameters.USERNAME_KEY, "invalid_user");
		invalidUserCredentials.put(AuthenticationParameters.PASSWORD_KEY, "incorrect password");
		facade.login(invalidUserCredentials);
	}
	
	@Test
	public void testGetCreateRemoveUserByAdmin() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
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
		assertEquals(SystemConstants.DEFAULT_PROFILE_PIC_ID, createdUser1.getProfile().getProfilePicId());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, createdUser1.getProfile().getProfilePic());
		User createdUser2 = usersAfterCreation.get(1);
		assertEquals(NEW_USERNAME_2, createdUser2.getUsername());
		assertEquals(NEW_USER_PASSWORD_2, createdUser2.getPassword());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_2, createdUser2.getProfile().getDescription());
		assertTrue(createdUser2.getProfile().getPosts().isEmpty());
		assertEquals(SystemConstants.DEFAULT_PROFILE_PIC_ID, createdUser2.getProfile().getProfilePicId());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, createdUser2.getProfile().getProfilePic());
		
		facade.removeUser(adminToken, usersAfterCreation.get(0).getUserId());
		
		List<User> usersAfterRemoval = facade.getUsers(adminToken);
		
		assertEquals(1, usersAfterRemoval.size());
	}
	
	@Test
	public void testLoginWorksCorrectlyAfterAttemptLoginWithInvalidUser() throws AuthenticationException, UnauthorizedOperationException {
		String adminToken = loginAsAdmin();
		
		List<User> users = facade.getUsers(adminToken);
		
		assertTrue(users.isEmpty());
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		Map<String, String> invalidUserCredentials = new HashMap<String, String>();
		invalidUserCredentials.put(AuthenticationParameters.USERNAME_KEY, "invalid_user");
		invalidUserCredentials.put(AuthenticationParameters.PASSWORD_KEY, "incorrect password");
		
		try {
			facade.login(invalidUserCredentials);
			Assert.fail("Expected exception.");
		} catch (AuthenticationException e) {

		}
		
		assertNotNull(loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1));
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotGetAllUsersWithNonAdminToken() throws UnauthorizedOperationException, AuthenticationException {	
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getUsers(userToken);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testCannotAddUserAsAdminWithNonAdminToken() throws UnauthorizedOperationException, AuthenticationException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.addUser(userToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotRemoveUser() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {		
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		facade.removeUser(userToken, usersAfterCreation.get(1).getUserId());
	}
	
	@Test
	public void testIsAdmin() throws AuthenticationException, UnauthorizedOperationException {
		String adminToken = loginAsAdmin();
		
		assertTrue(facade.userIsAdmin(ADMIN_USERNAME));
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> usersAfterCreation = facade.getUsers(adminToken);
		
		assertFalse(facade.userIsAdmin(usersAfterCreation.get(0).getUserId()));
		assertFalse(facade.userIsAdmin(usersAfterCreation.get(1).getUserId()));
	}
	
	@Test
	public void testGetAndCreateUserBySelf() throws UnauthorizedOperationException, AuthenticationException {
		String adminToken = loginAsAdmin();
		
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
		assertEquals(SystemConstants.DEFAULT_PROFILE_PIC_ID, createdUser.getProfile().getProfilePicId());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, createdUser.getProfile().getProfilePic());
	}
	
	@Test
	public void testCreateAndGetPostByAdmin() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<User> users = facade.getUsers(adminToken);
		
		List<Post> userPosts = facade.getUserPostsAdmin(adminToken, users.get(0).getUserId());
		assertTrue(userPosts.isEmpty());
		
		facade.createPost(userToken, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY, PICTURE_DATA);
		
		List<Post> userPostsAfterCreation = facade.getUserPostsAdmin(adminToken, users.get(0).getUserId());
		
		assertEquals(1, userPostsAfterCreation.size());
		Post createdPost = userPostsAfterCreation.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
		assertNotNull(createdPost.getPictureId());
		assertArrayEquals(PICTURE_DATA, createdPost.getPicture().getData());
		assertPicturePathExists(createdPost.getPicture());
	}

	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminUserCannotGetPostsByUserId() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException { 
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		User user1 = facade.getUsers(adminToken).get(0);
		
		facade.getUserPostsAdmin(userToken, user1.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminUserCannotGetPostsFromOtherUser() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException { 
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		User user2 = facade.getUsers(adminToken).get(1);
		
		facade.getUserPostsAdmin(userToken1, user2.getUserId());
	}
	
	@Test
	public void testCreateAndGetPostByNonAdmin() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<Post> userPosts = facade.getSelfPosts(userToken);
		assertTrue(userPosts.isEmpty());
		
		facade.createPost(userToken, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY, PICTURE_DATA);
		
		List<Post> userPostsAfterCreation = facade.getSelfPosts(userToken);
		
		assertEquals(1, userPostsAfterCreation.size());
		Post createdPost = userPostsAfterCreation.get(0);
		
		assertEquals(NEW_POST_TITLE, createdPost.getTitle());
		assertEquals(NEW_POST_CONTENT, createdPost.getContent());
		assertEquals(NEW_POST_VISIBILITY, createdPost.getVisibility());
		assertArrayEquals(PICTURE_DATA, createdPost.getPicture().getData());
		assertPicturePathExists(createdPost.getPicture());
	}
	
	@Test
	public void testGetOtherUserPostsByNonAdmin() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String user1Token = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		String user2Token = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		List<Post> user1Posts = facade.getUserPosts(user1Token, NEW_USERNAME_1);
		List<Post> user2PostsForUser1 = facade.getUserPosts(user1Token, NEW_USERNAME_2);
		List<Post> user2Posts = facade.getUserPosts(user2Token, NEW_USERNAME_2);
		List<Post> user1PostsForUser2 = facade.getUserPosts(user2Token, NEW_USERNAME_1);
		
		// no posts in the beginning
		assertTrue(user1Posts.isEmpty());
		assertTrue(user2Posts.isEmpty());
		assertTrue(user2PostsForUser1.isEmpty());
		assertTrue(user1PostsForUser2.isEmpty());
		
		facade.createPost(user1Token, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY, PICTURE_DATA);
		facade.createPost(user2Token, NEW_POST_TITLE_2, NEW_POST_CONTENT_2, NEW_POST_VISIBILITY_2, PICTURE_DATA_2);
		
		List<Post> user1PostsAfter = facade.getUserPosts(user1Token, NEW_USERNAME_1);
		
		// since they are not friends, each user can see their own posts, but not the other user's
		assertEquals(1, user1PostsAfter.size());
		assertEquals(NEW_POST_TITLE, user1PostsAfter.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT, user1PostsAfter.get(0).getContent());
		assertEquals(NEW_POST_VISIBILITY, user1PostsAfter.get(0).getVisibility());
		assertArrayEquals(PICTURE_DATA, user1PostsAfter.get(0).getPicture().getData());
		assertPicturePathExists(user1PostsAfter.get(0).getPicture());
		
		List<Post> user2PostsAfter = facade.getUserPosts(user2Token, NEW_USERNAME_2);
		
		assertEquals(1, user2PostsAfter.size());
		assertEquals(NEW_POST_TITLE_2, user2PostsAfter.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT_2, user2PostsAfter.get(0).getContent());
		assertEquals(NEW_POST_VISIBILITY_2, user2PostsAfter.get(0).getVisibility());
		assertArrayEquals(PICTURE_DATA_2, user2PostsAfter.get(0).getPicture().getData());
		assertPicturePathExists(user2PostsAfter.get(0).getPicture());
		
		List<Post> user2PostsForUser1After = facade.getUserPosts(user1Token, NEW_USERNAME_2);
		List<Post> user1PostsForUser2After = facade.getUserPosts(user2Token, NEW_USERNAME_1);
		
		assertTrue(user2PostsForUser1After.isEmpty());
		assertTrue(user1PostsForUser2After.isEmpty());
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());

		// after adding friendship, each user can see the other user's posts
		List<Post> user2PostsForUser1AfterFriendship = facade.getUserPosts(user1Token, NEW_USERNAME_2);
		
		assertEquals(1, user2PostsForUser1AfterFriendship.size());
		assertEquals(NEW_POST_TITLE_2, user2PostsForUser1AfterFriendship.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT_2, user2PostsForUser1AfterFriendship.get(0).getContent());
		assertEquals(NEW_POST_VISIBILITY_2, user2PostsForUser1AfterFriendship.get(0).getVisibility());
		assertArrayEquals(PICTURE_DATA_2, user2PostsForUser1AfterFriendship.get(0).getPicture().getData());
		assertPicturePathExists(user2PostsForUser1AfterFriendship.get(0).getPicture());
		
		List<Post> user1PostsForUser2AfterFriendship = facade.getUserPosts(user2Token, NEW_USERNAME_1);

		assertEquals(1, user1PostsForUser2AfterFriendship.size());
		assertEquals(NEW_POST_TITLE, user1PostsForUser2AfterFriendship.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT, user1PostsForUser2AfterFriendship.get(0).getContent());
		assertEquals(NEW_POST_VISIBILITY, user1PostsForUser2AfterFriendship.get(0).getVisibility());
		assertArrayEquals(PICTURE_DATA, user1PostsForUser2AfterFriendship.get(0).getPicture().getData());
		assertPicturePathExists(user1PostsForUser2AfterFriendship.get(0).getPicture());
	}
	
	@Test
	public void testAddFriendshipAdmin() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
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
	public void testNonAdminCannotAddFriendshipByUserId() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		facade.addFriendshipAdmin(userToken, user1.getUserId(), user2.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotGetFriendsById() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);

		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		
		facade.getFriends(userToken, user1.getUserId());
	}
	
	@Test
	public void testAddFriendshipRequestAndReject() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		assertTrue(facade.getSelfFriends(userToken1).isEmpty());
		assertTrue(facade.getSelfFriends(userToken2).isEmpty());
		
		List<FriendshipRequest> requestsSentBefore = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedBefore = facade.getReceivedFriendshipRequests(userToken2);
		assertTrue(requestsSentBefore.isEmpty());
		assertTrue(requestsReceivedBefore.isEmpty());
		
		facade.addFriendshipRequest(userToken1, NEW_USERNAME_2);
		
		List<FriendshipRequest> requestsSentAfter = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedAfter = facade.getReceivedFriendshipRequests(userToken2);
		assertEquals(1, requestsSentAfter.size());
		assertEquals(NEW_USERNAME_1, requestsSentAfter.get(0).getRequester().getUsername());
		assertEquals(NEW_USERNAME_2, requestsSentAfter.get(0).getRequested().getUsername());
		assertEquals(1, requestsReceivedAfter.size());
		assertEquals(NEW_USERNAME_1, requestsReceivedAfter.get(0).getRequester().getUsername());
		assertEquals(NEW_USERNAME_2, requestsReceivedAfter.get(0).getRequested().getUsername());
		
		facade.rejectFriendshipRequest(userToken2, NEW_USERNAME_1);
		
		List<FriendshipRequest> requestsSentAfterReject = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedAfterReject = facade.getReceivedFriendshipRequests(userToken2);
		assertTrue(facade.getSelfFriends(userToken1).isEmpty());
		assertTrue(facade.getSelfFriends(userToken2).isEmpty());
		assertTrue(requestsSentAfterReject.isEmpty());
		assertTrue(requestsReceivedAfterReject.isEmpty());
	}
	
	@Test
	public void testAddFriendshipRequestAndAccept() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		assertTrue(facade.getSelfFriends(userToken1).isEmpty());
		assertTrue(facade.getSelfFriends(userToken2).isEmpty());
		
		List<FriendshipRequest> requestsSentBefore = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedBefore = facade.getReceivedFriendshipRequests(userToken2);
		assertTrue(requestsSentBefore.isEmpty());
		assertTrue(requestsReceivedBefore.isEmpty());
		
		facade.addFriendshipRequest(userToken1, NEW_USERNAME_2);
		
		List<FriendshipRequest> requestsSentAfter = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedAfter = facade.getReceivedFriendshipRequests(userToken2);
		assertEquals(1, requestsSentAfter.size());
		assertEquals(NEW_USERNAME_1, requestsSentAfter.get(0).getRequester().getUsername());
		assertEquals(NEW_USERNAME_2, requestsSentAfter.get(0).getRequested().getUsername());
		assertEquals(1, requestsReceivedAfter.size());
		assertEquals(NEW_USERNAME_1, requestsReceivedAfter.get(0).getRequester().getUsername());
		assertEquals(NEW_USERNAME_2, requestsReceivedAfter.get(0).getRequested().getUsername());
		
		facade.acceptFriendshipRequest(userToken2, NEW_USERNAME_1);
		
		List<FriendshipRequest> requestsSentAfterAccept = facade.getSentFriendshipRequests(userToken1);
		List<FriendshipRequest> requestsReceivedAfterAccept = facade.getReceivedFriendshipRequests(userToken2);
		assertEquals(1, facade.getSelfFriends(userToken1).size());
		assertEquals(NEW_USERNAME_2, facade.getSelfFriends(userToken1).get(0).getUsername());
		assertEquals(1, facade.getSelfFriends(userToken2).size());
		assertEquals(NEW_USERNAME_1, facade.getSelfFriends(userToken2).get(0).getUsername());
		assertTrue(requestsSentAfterAccept.isEmpty());
		assertTrue(requestsReceivedAfterAccept.isEmpty());
	}
	
	@Test
	public void testAddFriendship() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFriendship(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> friends1 = facade.getSelfFriends(userToken1);
		List<UserSummary> friends2 = facade.getSelfFriends(userToken2);
		
		UserSummary user1Summary = new UserSummary(NEW_USERNAME_1, NEW_USER_PROFILE_DESCRIPTION_1, Picture.DEFAULT_PROFILE_PICTURE);
		UserSummary user2Summary = new UserSummary(NEW_USERNAME_2, NEW_USER_PROFILE_DESCRIPTION_2, Picture.DEFAULT_PROFILE_PICTURE);
		
		assertTrue(friends1.contains(user2Summary));
		assertTrue(friends2.contains(user1Summary));
	}
	
	@Test
	public void testGetFriendsPosts() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFriendship(userToken1, NEW_USERNAME_2);
		
		facade.createPost(userToken1, NEW_POST_TITLE, NEW_POST_CONTENT, NEW_POST_VISIBILITY, PICTURE_DATA);
		facade.createPost(userToken1, NEW_POST_TITLE_2, NEW_POST_CONTENT_2, NEW_POST_VISIBILITY_2, PICTURE_DATA_2);
		
		List<Post> postsFriendsUser1 = facade.getFriendsPosts(userToken1);
		List<Post> postsFriendsUser2 = facade.getFriendsPosts(userToken2);
		
		assertTrue(postsFriendsUser1.isEmpty());
		assertEquals(2, postsFriendsUser2.size());
		Post post1 = postsFriendsUser2.get(0);
		Post post2 = postsFriendsUser2.get(1);
		
		assertEquals(NEW_POST_TITLE, post1.getTitle());
		assertEquals(NEW_POST_CONTENT, post1.getContent());
		assertEquals(NEW_POST_VISIBILITY, post1.getVisibility());
		assertArrayEquals(PICTURE_DATA, post1.getPicture().getData());
		assertPicturePathExists(post1.getPicture());
		
		assertEquals(NEW_POST_TITLE_2, post2.getTitle());
		assertEquals(NEW_POST_CONTENT_2, post2.getContent());
		assertEquals(NEW_POST_VISIBILITY_2, post2.getVisibility());
		assertArrayEquals(PICTURE_DATA_2, post2.getPicture().getData());
		assertPicturePathExists(post2.getPicture());
	}
	
	@Test
	public void testGetFeedPosts() throws AuthenticationException, UnauthorizedOperationException, InterruptedException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.createPost(userToken2, NEW_POST_TITLE, NEW_POST_CONTENT, PostVisibility.PUBLIC, PICTURE_DATA);
		
		Thread.sleep(5);
		facade.createPost(userToken2, NEW_POST_TITLE_2, NEW_POST_CONTENT_2, PostVisibility.PUBLIC, PICTURE_DATA_2);
		
		Thread.sleep(5);
		facade.createPost(userToken2, NEW_POST_TITLE_3, NEW_POST_CONTENT_3, PostVisibility.PUBLIC, PICTURE_DATA_3);
		
		Thread.sleep(5);
		facade.createPost(userToken2, NEW_POST_TITLE_4, NEW_POST_CONTENT_4, PostVisibility.PRIVATE, PICTURE_DATA_4);
		
		List<Post> postsBeforeFollow = facade.getFeedPosts(userToken1);
		
		assertTrue(postsBeforeFollow.isEmpty());
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<Post> postsAfterFollow = facade.getFeedPosts(userToken1);
		
		assertEquals(2, postsAfterFollow.size());
		
		assertEquals(NEW_POST_TITLE_3, postsAfterFollow.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT_3, postsAfterFollow.get(0).getContent());
		assertArrayEquals(PICTURE_DATA_3, postsAfterFollow.get(0).getPicture().getData());
		assertPicturePathExists(postsAfterFollow.get(0).getPicture());
		
		assertEquals(NEW_POST_TITLE_2, postsAfterFollow.get(1).getTitle());
		assertEquals(NEW_POST_CONTENT_2, postsAfterFollow.get(1).getContent());
		assertArrayEquals(PICTURE_DATA_2, postsAfterFollow.get(1).getPicture().getData());
		assertPicturePathExists(postsAfterFollow.get(1).getPicture());
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<Post> postsAfterFriendship = facade.getFeedPosts(userToken1);
		
		assertEquals(2, postsAfterFriendship.size());
		
		assertEquals(NEW_POST_TITLE_4, postsAfterFriendship.get(0).getTitle());
		assertEquals(NEW_POST_CONTENT_4, postsAfterFriendship.get(0).getContent());
		assertArrayEquals(PICTURE_DATA_4, postsAfterFriendship.get(0).getPicture().getData());
		assertPicturePathExists(postsAfterFriendship.get(0).getPicture());
		
		assertEquals(NEW_POST_TITLE_3, postsAfterFriendship.get(1).getTitle());
		assertEquals(NEW_POST_CONTENT_3, postsAfterFriendship.get(1).getContent());
		assertArrayEquals(PICTURE_DATA_3, postsAfterFriendship.get(1).getPicture().getData());
		assertPicturePathExists(postsAfterFriendship.get(1).getPicture());
	}
	
	@Test
	public void testDeletePost() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		facade.createPost(userToken1, NEW_POST_TITLE, NEW_POST_CONTENT, PostVisibility.PRIVATE, PICTURE_DATA);
		
		List<Post> postsBeforeDelete = facade.getSelfPosts(userToken1);
		
		assertEquals(1, postsBeforeDelete.size());
		assertEquals(NEW_POST_TITLE, postsBeforeDelete.get(0).getTitle());
		
		facade.deletePost(userToken1, postsBeforeDelete.get(0).getId());
		
		List<Post> postsAfterDelete = facade.getSelfPosts(userToken1);
		
		assertTrue(postsAfterDelete.isEmpty());
	}
	
	@Test
	public void testAddFollowAdmin() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
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
	public void testNonAdminCannotFollowById() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		String userToken = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		facade.addFollowAdmin(userToken, user1.getUserId(), user2.getUserId());
	}
	
	@Test(expected = UnauthorizedOperationException.class)
	public void testNonAdminCannotGetFollowsById() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.getFollowedUsers(userToken1, user1.getUserId());
	}
	
	@Test
	public void testAddFollow() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);

		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		facade.addFollow(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> followsUser1 = facade.getFollowedUsers(userToken1);
		List<UserSummary> followsUser2 = facade.getFollowedUsers(userToken2);
		
		UserSummary user2Summary = new UserSummary(NEW_USERNAME_2, NEW_USER_PROFILE_DESCRIPTION_2, Picture.DEFAULT_PROFILE_PICTURE);
				
		assertEquals(1, followsUser1.size());
		assertTrue(followsUser1.contains(user2Summary));
		
		assertTrue(followsUser2.isEmpty());
	}
	
	@Test
	public void testGetUserSummaries() throws UnauthorizedOperationException, AuthenticationException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		List<UserSummary> summariesUser1 = facade.getUserSummaries(userToken1);
		List<UserSummary> summariesUser2 = facade.getUserSummaries(userToken2);
		
		assertEquals(1, summariesUser1.size());
		assertEquals(NEW_USERNAME_2, summariesUser1.get(0).getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_2, summariesUser1.get(0).getProfileDescription());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, summariesUser1.get(0).getProfilePicture());
		
		assertEquals(1, summariesUser2.size());
		assertEquals(NEW_USERNAME_1, summariesUser2.get(0).getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, summariesUser2.get(0).getProfileDescription());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, summariesUser2.get(0).getProfilePicture());
	}
	
	@Test
	public void testGetUserRecommendations() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		List<UserSummary> recommendationsUser1 = facade.getUserRecommendations(userToken1);
		List<UserSummary> recommendationsUser2 = facade.getUserRecommendations(userToken2);
		
		assertEquals(1, recommendationsUser1.size());
		assertEquals(NEW_USERNAME_2, recommendationsUser1.get(0).getUsername());
		
		assertEquals(1, recommendationsUser2.size());
		assertEquals(NEW_USERNAME_1, recommendationsUser2.get(0).getUsername());
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<UserSummary> recommendationsUser1AfterFriendship = facade.getUserRecommendations(userToken1);
		List<UserSummary> recommendationsUser2AfterFriendship = facade.getUserRecommendations(userToken2);
		
		assertTrue(recommendationsUser1AfterFriendship.isEmpty());
		assertTrue(recommendationsUser2AfterFriendship.isEmpty());
	}
	
	@Test
	public void testGetFollowRecommendations() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		List<UserSummary> recommendationsUser1 = facade.getFollowRecommendations(userToken1);
		List<UserSummary> recommendationsUser2 = facade.getFollowRecommendations(userToken2);
		
		assertEquals(1, recommendationsUser1.size());
		assertEquals(NEW_USERNAME_2, recommendationsUser1.get(0).getUsername());
		
		assertEquals(1, recommendationsUser2.size());
		assertEquals(NEW_USERNAME_1, recommendationsUser2.get(0).getUsername());
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<UserSummary> recommendationsUser1AfterFollow = facade.getFollowRecommendations(userToken1);
		List<UserSummary> recommendationsUser2AfterFollow = facade.getFollowRecommendations(userToken2);
		
		assertTrue(recommendationsUser1AfterFollow.isEmpty());
		
		assertEquals(1, recommendationsUser2AfterFollow.size());
		assertEquals(NEW_USERNAME_1, recommendationsUser2.get(0).getUsername());
	}
	
	@Test
	public void testIsFriend() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		assertFalse(facade.isFriend(userToken1, NEW_USERNAME_2));
		assertFalse(facade.isFriend(userToken2, NEW_USERNAME_1));
		
		assertFalse(facade.isFriend(userToken1, NEW_USERNAME_1));
		assertFalse(facade.isFriend(userToken2, NEW_USERNAME_2));
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		assertTrue(facade.isFriend(userToken1, NEW_USERNAME_2));
		assertTrue(facade.isFriend(userToken2, NEW_USERNAME_1));
	}
	
	@Test
	public void testGetSelf() throws AuthenticationException, UnauthorizedOperationException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		UserSummary self1 = facade.getSelf(userToken1);
		UserSummary self2 = facade.getSelf(userToken2);
		
		assertEquals(NEW_USERNAME_1, self1.getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_1, self1.getProfileDescription());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, self1.getProfilePicture());
		assertEquals(NEW_USERNAME_2, self2.getUsername());
		assertEquals(NEW_USER_PROFILE_DESCRIPTION_2, self2.getProfileDescription());
		assertEquals(Picture.DEFAULT_PROFILE_PICTURE, self2.getProfilePicture());
	}
	
	@Test
	public void testFollows() throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);	
		String userToken2 = loginAsUser(NEW_USERNAME_2, NEW_USER_PASSWORD_2);
		
		assertFalse(facade.follows(userToken1, NEW_USERNAME_1));
		assertFalse(facade.follows(userToken1, NEW_USERNAME_2));
		assertFalse(facade.follows(userToken2, NEW_USERNAME_1));
		assertFalse(facade.follows(userToken2, NEW_USERNAME_2));
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		assertFalse(facade.follows(userToken1, NEW_USERNAME_1));
		assertTrue(facade.follows(userToken1, NEW_USERNAME_2));
		assertFalse(facade.follows(userToken2, NEW_USERNAME_1));
		assertFalse(facade.follows(userToken2, NEW_USERNAME_2));
	}
	
	@Test
	public void testUnfollow() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.addFollowAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<UserSummary> followsBefore = facade.getFollowedUsers(userToken1);
		
		assertEquals(1, followsBefore.size());
		assertEquals(NEW_USERNAME_2, followsBefore.get(0).getUsername());
		
		facade.unfollow(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> followsAfter = facade.getFollowedUsers(userToken1);
		
		assertTrue(followsAfter.isEmpty());
	}
	
	@Test
	public void testUnfriend() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		facade.addUser(adminToken, NEW_USERNAME_2, NEW_USER_PASSWORD_2, NEW_USER_PROFILE_DESCRIPTION_2);
		
		List<User> users = facade.getUsers(adminToken);
		User user1 = users.get(0);
		User user2 = users.get(1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.addFriendshipAdmin(adminToken, user1.getUserId(), user2.getUserId());
		
		List<UserSummary> friendsBefore = facade.getSelfFriends(userToken1);
		
		assertEquals(1, friendsBefore.size());
		assertEquals(NEW_USERNAME_2, friendsBefore.get(0).getUsername());
		
		facade.unfriend(userToken1, NEW_USERNAME_2);
		
		List<UserSummary> friendsAfter = facade.getSelfFriends(userToken1);
		
		assertTrue(friendsAfter.isEmpty());		
	}
	
	@Test
	public void testChangeAndGetProfilePic() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		assertNull(facade.getUserPic(userToken1, NEW_USERNAME_1));
		
		byte[] profilePicData = new byte[] {1, 1, 1};
		
		facade.changeSelfProfilePic(userToken1, profilePicData);
		
		assertArrayEquals(new byte[] {1, 1, 1}, facade.getUserPic(userToken1, NEW_USERNAME_1));
	}
	
	@Test
	public void testUpdateProfile() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		String adminToken = loginAsAdmin();
		
		facade.addUser(adminToken, NEW_USERNAME_1, NEW_USER_PASSWORD_1, NEW_USER_PROFILE_DESCRIPTION_1);
		
		String userToken1 = loginAsUser(NEW_USERNAME_1, NEW_USER_PASSWORD_1);
		
		facade.updateProfile(userToken1, UPDATED_USER_PROFILE_DESCRIPTION_1, new byte[] {1, 1, 1});
		
		UserSummary userAfterUpdate = facade.getSelf(userToken1);
		
		assertEquals(UPDATED_USER_PROFILE_DESCRIPTION_1, userAfterUpdate.getProfileDescription());
		assertArrayEquals(new byte[] {1, 1, 1}, userAfterUpdate.getProfilePicture().getData());
	}
	
	private String loginAsAdmin() throws AuthenticationException {
		Map<String, String> adminCredentials = new HashMap<String, String>();
		adminCredentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		adminCredentials.put(AuthenticationParameters.PASSWORD_KEY, ADMIN_PASSWORD);
		
		return facade.login(adminCredentials);
	}
	
	private String loginAsUser(String username, String password) throws AuthenticationException {
		Map<String, String> userCredentials = new HashMap<String, String>();
		userCredentials.put(AuthenticationParameters.USERNAME_KEY, username);
		userCredentials.put(AuthenticationParameters.PASSWORD_KEY, password);
		
		return facade.login(userCredentials);
	}

	private void assertPicturePathExists(Picture picture) {
		String pictureRelativePath = picture.getPath();
		String pictureFullPath = TEST_CACHE_PATH + File.separator + pictureRelativePath; 
		assertNotNull(pictureRelativePath);
		assertTrue(new File(pictureFullPath).exists());
	}
	
	@After
	@Override
	public void tearDown() throws IOException {
		propertiesUtilMock.close();
		pathsUtilMock.close();
		ApplicationFacade.getInstance().shutdown();
		super.tearDown();
	}
}
