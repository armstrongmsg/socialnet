package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.ConfigurationPropertiesDefaults;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.core.authentication.AuthenticationPlugin;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.core.authorization.AuthorizationPlugin;
import com.armstrongmsg.socialnet.core.authorization.Operation;
import com.armstrongmsg.socialnet.core.authorization.OperationOnUser;
import com.armstrongmsg.socialnet.core.authorization.OperationType;
import com.armstrongmsg.socialnet.core.feed.FeedPolicy;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO many methods can use smarter queries to the database
public class Network {
	private Admin admin;
	private AuthenticationPlugin authenticationPlugin;
	private AuthorizationPlugin authorizationPlugin;
	private FeedPolicy feedPolicy;
	private StorageFacade storageFacade;
	
	private static Logger logger = LoggerFactory.getLogger(Network.class);
	
	public Network(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships, 
			List<Follow> follows, AuthenticationPlugin authenticationPlugin, AuthorizationPlugin authorizationPlugin) {
		this.admin = admin;
		this.authenticationPlugin = authenticationPlugin;
		this.authorizationPlugin = authorizationPlugin;
	}

	public Network(StorageFacade storageManager) throws FatalErrorException {
		this.storageFacade = storageManager;
		
		String adminUsername = ConfigurationPropertiesDefaults.DEFAULT_ADMIN_USERNAME;
		String adminPassword = ConfigurationPropertiesDefaults.DEFAULT_ADMIN_PASSWORD;
		
		logger.info(Messages.Logging.LOADING_ADMIN_CONFIGURATION);
		PropertiesUtil properties = PropertiesUtil.getInstance();
		adminUsername = properties.getProperty(ConfigurationProperties.ADMIN_USERNAME);
		adminPassword = properties.getProperty(ConfigurationProperties.ADMIN_PASSWORD);
		logger.info(Messages.Logging.LOADED_ADMIN, adminUsername);

		Admin admin = new Admin(adminUsername, adminUsername, adminPassword);
		ClassFactory classFactory = new ClassFactory();
		String authenticationPluginClassName = properties.getProperty(ConfigurationProperties.AUTHENTICATION_PLUGIN_CLASS_NAME);
		String authorizationPluginClassName = properties.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME);
		String feedPolicyClassName = properties.getProperty(ConfigurationProperties.FEED_POLICY_CLASS_NAME);
		
		logger.info(Messages.Logging.LOADING_AUTHENTICATION_PLUGIN);
		
		AuthenticationPlugin authenticationPlugin = (AuthenticationPlugin) 
				classFactory.createInstance(authenticationPluginClassName, this.storageFacade);
		authenticationPlugin.setUp(admin);
		
		logger.info(Messages.Logging.LOADING_AUTHORIZATION_PLUGIN);
		
		AuthorizationPlugin authorizationPlugin = (AuthorizationPlugin) 
				classFactory.createInstance(authorizationPluginClassName, this.storageFacade);
		authorizationPlugin.setUp(admin);
		
		logger.info(Messages.Logging.LOADING_FEED_POLICY);
		
		this.feedPolicy = (FeedPolicy) classFactory.createInstance(feedPolicyClassName);
		
		this.admin = admin;
		this.authenticationPlugin = authenticationPlugin;
		this.authorizationPlugin = authorizationPlugin;
	}

	public Admin getAdmin() {
		return admin;
	}

	public List<User> getUsers(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_ALL_USERS));
		
		return this.storageFacade.getAllUsers();
	}

	public void addUser(UserToken userToken, String username, String password, String profileDescription) throws UnauthorizedOperationException, AuthenticationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_USER));
		
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		UUID uuid = UUID.randomUUID();
		User newUser = new User(uuid.toString(), username, password, profile);
		this.storageFacade.saveUser(newUser);
	}
	
	public void addUser(String username, String password, String profileDescription) {
		UUID uuid = UUID.randomUUID();
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		User newUser = new User(uuid.toString(), username, password, profile);
		this.storageFacade.saveUser(newUser);
	}

	public void removeUser(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.REMOVE_USER));
		this.storageFacade.removeUserById(userId);
	}

	// TODO test
	private User findUserById(String userId) throws UserNotFoundException {
		if (admin.getUserId().equals(userId)) {
			return admin;
		}

		try {
			return this.storageFacade.getUserById(userId);
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException(String.format(Messages.Exception.COULD_NOT_FIND_USER, userId));
		}
	}

	public void createPost(UserToken userToken, String title, String content, PostVisibility newPostVisibility, byte[] pictureData) throws AuthenticationException, UserNotFoundException {
		User user = this.authenticationPlugin.getUser(userToken);
		Picture postPicture = null;
		
		if (pictureData != null) {
			postPicture = new Picture(UUID.randomUUID().toString(), pictureData);
		}
		
		user.getProfile().createPost(title, content, newPostVisibility, postPicture);
		this.storageFacade.updateUser(user);
	}

	public List<Post> getUserPostsAdmin(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		User target = findUserById(userId);
		this.authorizationPlugin.authorize(requester, new OperationOnUser(OperationType.GET_USER_POSTS_ADMIN, target));
		
		User user = findUserById(userId);
		return user.getProfile().getPosts();
	}
	
	public List<Post> getSelfPosts(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new OperationOnUser(OperationType.GET_SELF_POSTS, requester));
		return requester.getProfile().getPosts();
	}

	public void addFriendshipAdmin(UserToken userToken, String userId1, String userId2) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP_ADMIN));
		
		User user1 = findUserById(userId1);
		User user2 = findUserById(userId2);
		this.storageFacade.saveFriendship(new Friendship(user1, user2));
	}

	public void addFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP_REQUEST));
		
		User friend = findUserByUsername(username);
		this.storageFacade.saveFriendshipRequest(new FriendshipRequest(requester, friend));
	}
	
	public List<FriendshipRequest> getSentFriendshipRequests(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_SENT_FRIENDSHIP_REQUESTS));
		
		return this.storageFacade.getSentFrienshipRequestsById(requester.getUserId());
	}
	
	public List<FriendshipRequest> getReceivedFriendshipRequests(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_RECEIVED_FRIENDSHIP_REQUESTS));
		
		return this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId());
	}
	
	public void acceptFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ACCEPT_FRIENDSHIP_REQUEST));
		
		FriendshipRequest request = this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId(), username);
		
		if (request == null) {
			throw new UnauthorizedOperationException(
					String.format(Messages.Exception.FRIENDSHIP_REQUEST_NOT_FOUND, userToken, username));
		}
		
		User friend = findUserByUsername(username);
		this.storageFacade.saveFriendship(new Friendship(requester, friend));
		this.storageFacade.removeFriendshipRequest(request);
	}
	
	public void rejectFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.REJECT_FRIENDSHIP_REQUEST));
		
		FriendshipRequest request = this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId(), username);
		
		if (request == null) {
			throw new UnauthorizedOperationException(
					String.format(Messages.Exception.FRIENDSHIP_REQUEST_NOT_FOUND, userToken, username));
		}
		
		this.storageFacade.removeFriendshipRequest(request);
	}
	
	public void addFriendship(UserToken userToken, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP));
		
		User friend = findUserByUsername(username);
		this.storageFacade.saveFriendship(new Friendship(requester, friend));
	}
	
	public List<User> getFriends(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS_ADMIN));
		return this.doGetUserFriends(userId);
	}
	
	private List<User> doGetUserFriends(String userId) throws UserNotFoundException {
		User user = findUserById(userId);
		
		List<Friendship> userFriendships = this.storageFacade.getFriendshipsByUserId(userId);
		List<User> friends = new ArrayList<User>();
		
		for (Friendship friendship : userFriendships) {
			if (friendship.getFriend1().equals(user)) {
				friends.add(friendship.getFriend2());
			}
			
			if (friendship.getFriend2().equals(user)) {
				friends.add(friendship.getFriend1());
			}
		}
		
		return friends;
	}
	
	public List<UserSummary> getSelfFriends(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS));
		return this.doGetSelfFriends(requester);
	}
	
	private List<UserSummary> doGetSelfFriends(User requester) {
		List<Friendship> userFriendships = this.storageFacade.getFriendshipsByUserId(requester.getUserId());
		List<UserSummary> friends = new ArrayList<UserSummary>();
		
		for (Friendship friendship : userFriendships) {
			if (friendship.getFriend1().equals(requester)) {
				User friend = friendship.getFriend2();
				UserSummary summary = new UserSummary(friend.getUsername(), friend.getProfile().getDescription(), friend.getProfile().getProfilePic());
				friends.add(summary);
			}
			
			if (friendship.getFriend2().equals(requester)) {
				User friend = friendship.getFriend1();
				UserSummary summary = new UserSummary(friend.getUsername(), friend.getProfile().getDescription(), friend.getProfile().getProfilePic());
				friends.add(summary);
			}
		}
		
		return friends;
	}

	public void addFollowAdmin(UserToken userToken, String followerId, String followedId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FOLLOW_ADMIN));
		
		User follower = findUserById(followerId);
		User followed = findUserById(followedId);
		this.storageFacade.saveFollow(new Follow(follower, followed));
	}
	
	public void addFollow(UserToken userToken, String followedUsername) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FOLLOW));
		
		User followed = findUserByUsername(followedUsername);
		this.storageFacade.saveFollow(new Follow(requester, followed));
	}
	
	public List<User> getFollowedUsers(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOWED_USERS_ADMIN));
		return this.doGetUserFollowedUsers(userId);
	}

	private List<User> doGetUserFollowedUsers(String userId) throws UserNotFoundException {
		User user = findUserById(userId);
		List<Follow> userFollows = this.storageFacade.getFollowsByUserId(userId);
		List<User> followedUsers = new ArrayList<User>();
		
		for (Follow follow : userFollows) {
			if (follow.getFollower().equals(user)) {
				followedUsers.add(follow.getFollowed());
			}
		}
		
		return followedUsers;
	}

	public List<UserSummary> getFollowedUsers(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOWED_USERS));
		return doGetFollowedUsers(requester);
	}
	
	private List<UserSummary> doGetFollowedUsers(User requester) {
		List<Follow> userFollows = this.storageFacade.getFollowsByUserId(requester.getUserId());
		List<UserSummary> followedUsers = new ArrayList<UserSummary>();
		
		for (Follow follow : userFollows) {
			if (follow.getFollower().equals(requester)) {
				User followed = follow.getFollowed();
				followedUsers.add(new UserSummary(followed.getUsername(), followed.getProfile().getDescription(), followed.getProfile().getProfilePic()));
			}
		}
		
		return followedUsers;
	}

	public boolean userIsAdmin(String userId) {
		return this.admin.getUserId().equals(userId);
	}

	public List<Post> getFriendsPosts(UserToken token) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS_POSTS));
		return doGetFriendsPosts(requester);
	}
	
	private List<Post> doGetFriendsPosts(User requester) throws UserNotFoundException {
		List<User> friends = this.doGetUserFriends(requester.getUserId());
		List<Post> friendsPosts = new ArrayList<Post>();
		
		for (User friend : friends) {
			friendsPosts.addAll(friend.getProfile().getPosts());
		}
		
		return friendsPosts;
	}
	
	private List<Post> doGetFollowedPosts(User requester) throws UserNotFoundException {
		List<User> followedUsers = doGetUserFollowedUsers(requester.getUserId());
		List<Post> followedUsersPublicPosts = new ArrayList<Post>();
		
		for (User followedUser : followedUsers) {
			List<Post> followedUserPosts = followedUser.getProfile().getPosts();
			
			for (Post post : followedUserPosts) {
				if (post.getVisibility().equals(PostVisibility.PUBLIC)) {
					followedUsersPublicPosts.add(post);
				}
			}
		}
		
		return followedUsersPublicPosts;
	}
	
	public List<Post> getFeedPosts(UserToken token) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FEED_POSTS));
		List<Post> friendsPosts = doGetFriendsPosts(requester);
		List<Post> followedUsersPosts = doGetFollowedPosts(requester);
		List<Post> feedCandidatePosts = new ArrayList<Post>();
		feedCandidatePosts.addAll(friendsPosts);
		feedCandidatePosts.addAll(followedUsersPosts);
		return this.feedPolicy.filter(feedCandidatePosts);
	}

	public List<Post> getUserPosts(UserToken token, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_POSTS));
		
		if (doIsFriend(requester, username) || requester.getUsername().equals(username)) {
			User user = findUserByUsername(username);
			return user.getProfile().getPosts();
		}
		
		return new ArrayList<Post>();
	}
	
	public void deletePost(UserToken token, String postId) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.DELETE_POST));
		
		List<Post> posts = requester.getProfile().getPosts();
		Post postToDelete = null;
		
		for (Post post : posts) {
			if (post.getId().equals(postId)) {
				postToDelete = post;
			}
		}
		
		posts.remove(postToDelete);
		this.storageFacade.updateUser(requester);
	}
	
	public UserToken login(Map<String, String> credentials) throws AuthenticationException {
		return this.authenticationPlugin.authenticate(credentials);
	}

	// TODO test
	private User findUserByUsername(String username) throws UserNotFoundException {
		if (admin.getUsername().equals(username)) {
			return admin;
		}
		
		try {
			return this.storageFacade.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException(String.format(Messages.Exception.COULD_NOT_FIND_USER, username)); 
		}
	}

	public List<UserSummary> getUserSummaries(UserToken token) throws UnauthorizedOperationException, AuthenticationException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_SUMMARIES));
		
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			if (!user.equals(requester)) {
				userSummaries.add(new UserSummary(user.getUsername(), user.getProfile().getDescription(), user.getProfile().getProfilePic()));
			}
		}

		return userSummaries;
	}

	public List<UserSummary> getUserRecommendations(UserToken token) throws UnauthorizedOperationException, AuthenticationException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_RECOMMENDATIONS));
		
		List<UserSummary> friends = doGetSelfFriends(requester);
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			UserSummary userSummary = new UserSummary(user.getUsername(), user.getProfile().getDescription(), user.getProfile().getProfilePic()); 
			
			if (!user.equals(requester) && !friends.contains(userSummary)) {
				userSummaries.add(userSummary);
			}
		}
		
		return userSummaries;
	}

	public List<UserSummary> getFollowRecommendations(UserToken token) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOW_RECOMMENDATIONS));
		
		List<UserSummary> follows = this.doGetFollowedUsers(requester);
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			UserSummary userSummary = new UserSummary(user.getUsername(), user.getProfile().getDescription(), user.getProfile().getProfilePic()); 
			
			if (!user.equals(requester) && !follows.contains(userSummary)) {
				userSummaries.add(userSummary);
			}
		}
		
		return userSummaries;
	}

	public boolean isFriend(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.IS_FRIEND));
		return doIsFriend(requester, username);
	}
	
	private boolean doIsFriend(User requester, String username) {
		List<UserSummary> friends = doGetSelfFriends(requester);
		
		for (UserSummary friend : friends) {
			if (friend.getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	public UserSummary getSelf(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_SELF));
		UserSummary summary = new UserSummary(requester.getUsername(), requester.getProfile().getDescription(), requester.getProfile().getProfilePic());
		return summary;
	}

	public boolean follows(UserToken userToken, String username) throws UnauthorizedOperationException, AuthenticationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.FOLLOWS));
		List<UserSummary> follows = doGetFollowedUsers(requester);
		
		for (UserSummary summary : follows) {
			if (summary.getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}

	public void unfollow(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UNFOLLOW));
		
		// TODO should query the database for the exact follow object
		List<Follow> follows = this.storageFacade.getFollowsByUserId(requester.getUserId());
		
		for (Follow follow : follows) {
			if (follow.getFollowed().getUsername().equals(username)) {
				this.storageFacade.removeFollow(follow);
			}
		}
	}

	public void unfriend(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UNFRIEND));
		
		// TODO should query the database for the exact friendship object
		List<Friendship> friendships = this.storageFacade.getFriendshipsByUserId(requester.getUserId());
		
		for (Friendship friendship : friendships) {
			if (friendship.getFriend1().getUsername().equals(username)) {
				this.storageFacade.removeFriendship(friendship);
			}
			
			if (friendship.getFriend2().getUsername().equals(username)) {
				this.storageFacade.removeFriendship(friendship);
			}
		}
	}

	public void changeSelfProfilePic(UserToken userToken, byte[] picData) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.CHANGE_SELF_PROFILE_PIC));
		doChangeSelfProfilePic(requester, picData);
		this.storageFacade.updateUser(requester);
	}

	private void doChangeSelfProfilePic(User requester, byte[] picData) {
		String profilePicId = UUID.randomUUID().toString();
		Picture newProfilePic = new Picture(profilePicId, picData);
		requester.getProfile().setProfilePic(newProfilePic);
		requester.getProfile().setProfilePicId(profilePicId);
	}

	public byte[] getUserProfilePic(UserToken loggedUserToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(loggedUserToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_PROFILE_PIC));
		
		User user = findUserByUsername(username);
		return user.getProfile().getProfilePic().getData();
	}

	public void updateProfile(UserToken userToken, String profileDescription, byte[] picData) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UPDATE_USER_PROFILE));
		requester.getProfile().setDescription(profileDescription);
		doChangeSelfProfilePic(requester, picData);
		this.storageFacade.updateUser(requester);
	}
}
