package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.ConfigurationPropertiesDefaults;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.core.authentication.AuthenticationPlugin;
import com.armstrongmsg.socialnet.core.authorization.AuthorizationPlugin;
import com.armstrongmsg.socialnet.core.authorization.Operation;
import com.armstrongmsg.socialnet.core.authorization.OperationOnUser;
import com.armstrongmsg.socialnet.core.authorization.OperationType;
import com.armstrongmsg.socialnet.core.feed.FeedPolicy;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.storage.MediaStorageFacade;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO split this class
public class Network {
	private Admin admin;
	private AuthenticationPlugin authenticationPlugin;
	private AuthorizationPlugin authorizationPlugin;
	private FeedPolicy feedPolicy;
	private StorageFacade storageFacade;
	private MediaStorageFacade mediaStorageFacade;
	
	private static Logger logger = LoggerFactory.getLogger(Network.class);
	
	public Network(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships, 
			List<Follow> follows, AuthenticationPlugin authenticationPlugin, AuthorizationPlugin authorizationPlugin) {
		this.admin = admin;
		this.authenticationPlugin = authenticationPlugin;
		this.authorizationPlugin = authorizationPlugin;
	}

	public Network(StorageFacade storageManager, MediaStorageFacade mediaStorageFacade) throws FatalErrorException {
		this.storageFacade = storageManager;
		this.mediaStorageFacade = mediaStorageFacade;
		
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

	public List<User> getUsers(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_ALL_USERS));
		
		return this.storageFacade.getAllUsers();
	}

	public void addUser(String userToken, String username, String password, String profileDescription) 
			throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, UserAlreadyExistsException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_USER));
		
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		UUID uuid = UUID.randomUUID();
		User newUser = new User(uuid.toString(), username, password, profile);
		this.storageFacade.saveUser(newUser);
	}
	
	public void addUser(String username, String password, String profileDescription) throws InternalErrorException, UserAlreadyExistsException {
		UUID uuid = UUID.randomUUID();
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		User newUser = new User(uuid.toString(), username, password, profile);
		this.storageFacade.saveUser(newUser);
	}

	public void removeUser(String userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.REMOVE_USER));
		this.storageFacade.removeUserById(userId);
	}

	private User findUserById(String userId) throws UserNotFoundException, InternalErrorException {
		if (admin.getUserId().equals(userId)) {
			return admin;
		}

		try {
			return this.storageFacade.getUserById(userId);
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException(String.format(Messages.Exception.COULD_NOT_FIND_USER, userId));
		}
	}

	public void createPost(String userToken, String title, String content, PostVisibility newPostVisibility, byte[] pictureData) 
			throws AuthenticationException, UserNotFoundException, InternalErrorException, UnauthorizedOperationException {
		User user = this.authenticationPlugin.getUser(userToken);

		String pictureId = createMedia(user, pictureData);
		List<String> pictureIds = new ArrayList<String>();
		pictureIds.add(pictureId);
		
		user.getProfile().createPost(title, content, newPostVisibility, pictureIds);
		this.storageFacade.updateUser(user);
	}
	
	public List<Post> getUserPostsAdmin(String userToken, String userId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		User target = findUserById(userId);
		this.authorizationPlugin.authorize(requester, new OperationOnUser(OperationType.GET_USER_POSTS_ADMIN, target));
		
		User user = findUserById(userId);
		return user.getProfile().getPosts();
	}
	
	public List<Post> getSelfPosts(String userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new OperationOnUser(OperationType.GET_SELF_POSTS, requester));
		return requester.getProfile().getPosts();
	}

	public void addFriendshipAdmin(String userToken, String userId1, String userId2) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, FriendshipAlreadyExistsException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP_ADMIN));
		
		User user1 = findUserById(userId1);
		User user2 = findUserById(userId2);
		this.storageFacade.saveFriendship(new Friendship(user1, user2));
	}

	public void addFriendshipRequest(String userToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FriendshipRequestAlreadyExistsException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP_REQUEST));
		
		if (!hasSentFriendshipRequestTo(requester, username) && 
				!hasReceivedFriendshipRequestFrom(requester, username)) {
			User friend = findUserByUsername(username);
			this.storageFacade.saveFriendshipRequest(new FriendshipRequest(requester, friend));
		} else {
			logger.debug(Messages.Logging.FRIENDSHIP_REQUEST_ALREADY_RECEIVED_IGNORING_REQUEST, 
					requester.getUserId(), username);
		}
	}
	
	private boolean hasSentFriendshipRequestTo(User requester, String username) throws InternalErrorException {
		List<FriendshipRequest> sentFriendshipRequests = this.storageFacade.getSentFrienshipRequestsById(requester.getUserId());
		for (FriendshipRequest request : sentFriendshipRequests) {
			if (request.getRequested().getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasReceivedFriendshipRequestFrom(User requester, String username) throws InternalErrorException {
		List<FriendshipRequest> receivedFriendshipRequests = this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId());
		for (FriendshipRequest request : receivedFriendshipRequests) {
			if (request.getRequester().getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<FriendshipRequest> getSentFriendshipRequests(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_SENT_FRIENDSHIP_REQUESTS));
		
		return this.storageFacade.getSentFrienshipRequestsById(requester.getUserId());
	}
	
	public List<FriendshipRequest> getReceivedFriendshipRequests(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_RECEIVED_FRIENDSHIP_REQUESTS));
		
		return this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId());
	}
	
	public void acceptFriendshipRequest(String userToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FriendshipRequestNotFound, InternalErrorException, FriendshipAlreadyExistsException {
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
	
	public void rejectFriendshipRequest(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, FriendshipRequestNotFound, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.REJECT_FRIENDSHIP_REQUEST));
		
		FriendshipRequest request = this.storageFacade.getReceivedFrienshipRequestsById(requester.getUserId(), username);
		
		if (request == null) {
			throw new UnauthorizedOperationException(
					String.format(Messages.Exception.FRIENDSHIP_REQUEST_NOT_FOUND, userToken, username));
		}
		
		this.storageFacade.removeFriendshipRequest(request);
	}
	
	public void addFriendship(String userToken, String username) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, FriendshipAlreadyExistsException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP));
		
		User friend = findUserByUsername(username);
		this.storageFacade.saveFriendship(new Friendship(requester, friend));
	}
	
	public List<User> getFriends(String userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS_ADMIN));
		return this.doGetUserFriends(userId);
	}
	
	private List<User> doGetUserFriends(String userId) throws UserNotFoundException, InternalErrorException {
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
	
	public List<UserSummary> getSelfFriends(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS));
		return this.doGetSelfFriends(requester);
	}
	
	private List<UserSummary> doGetSelfFriends(User requester) throws InternalErrorException, MediaNotFoundException, UnauthorizedOperationException {
		List<Friendship> userFriendships = this.storageFacade.getFriendshipsByUserId(requester.getUserId());
		List<UserSummary> friends = new ArrayList<UserSummary>();
		
		for (Friendship friendship : userFriendships) {
			UserSummary summary = null;
			
			if (friendship.getFriend1().equals(requester)) {				
				summary = getUserSummary(requester, friendship.getFriend2());
			}
			
			if (friendship.getFriend2().equals(requester)) {
				summary = getUserSummary(requester, friendship.getFriend1());
			}
			
			friends.add(summary);
		}
		
		return friends;
	}
	
	private UserSummary getUserSummary(User requester, User user) throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		String pictureUri = getProfilePicPath(requester, user);
		return new UserSummary(user.getUsername(), user.getProfile().getDescription(), 
				user.getProfile().getProfilePicId(), pictureUri);
	}

	private String getProfilePicPath(User requester, User friend)
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		String pictureUri = SystemConstants.DEFAULT_PROFILE_PIC_PATH;
		if (!friend.getProfile().getProfilePicId().equals(SystemConstants.DEFAULT_PROFILE_PIC_ID)) {
			pictureUri = mediaStorageFacade.getMediaUri(requester.getUsername(), friend.getProfile().getProfilePicId());
		}
		return pictureUri;
	}

	public void addFollowAdmin(String userToken, String followerId, String followedId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, FollowAlreadyExistsException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FOLLOW_ADMIN));
		
		User follower = findUserById(followerId);
		User followed = findUserById(followedId);
		this.storageFacade.saveFollow(new Follow(follower, followed));
	}
	
	public void addFollow(String userToken, String followedUsername) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FollowAlreadyExistsException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FOLLOW));
		
		User followed = findUserByUsername(followedUsername);
		this.storageFacade.saveFollow(new Follow(requester, followed));
	}
	
	public List<User> getFollowedUsers(String userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOWED_USERS_ADMIN));
		return this.doGetUserFollowedUsers(userId);
	}

	private List<User> doGetUserFollowedUsers(String userId) throws UserNotFoundException, InternalErrorException {
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

	public List<UserSummary> getFollowedUsers(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOWED_USERS));
		return doGetFollowedUsers(requester);
	}
	
	private List<UserSummary> doGetFollowedUsers(User requester) throws InternalErrorException, MediaNotFoundException, UnauthorizedOperationException {
		List<Follow> userFollows = this.storageFacade.getFollowsByUserId(requester.getUserId());
		List<UserSummary> followedUsers = new ArrayList<UserSummary>();
		
		for (Follow follow : userFollows) {
			if (follow.getFollower().equals(requester)) {
				UserSummary summary = getUserSummary(requester, follow.getFollowed());
				followedUsers.add(summary);
			}
		}
		
		return followedUsers;
	}

	public boolean userIsAdmin(String userId) {
		return this.admin.getUserId().equals(userId);
	}

	public List<Post> getFriendsPosts(String token) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS_POSTS));
		return doGetFriendsPosts(requester);
	}
	
	private List<Post> doGetFriendsPosts(User requester) throws UserNotFoundException, InternalErrorException {
		List<User> friends = this.doGetUserFriends(requester.getUserId());
		List<Post> friendsPosts = new ArrayList<Post>();
		
		for (User friend : friends) {
			friendsPosts.addAll(friend.getProfile().getPosts());
		}
		
		return friendsPosts;
	}
	
	private List<Post> doGetFollowedPosts(User requester) throws UserNotFoundException, InternalErrorException {
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
	
	public List<Post> getFeedPosts(String token) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FEED_POSTS));
		List<Post> friendsPosts = doGetFriendsPosts(requester);
		List<Post> followedUsersPosts = doGetFollowedPosts(requester);
		List<Post> feedCandidatePosts = new ArrayList<Post>();
		feedCandidatePosts.addAll(friendsPosts);
		feedCandidatePosts.addAll(followedUsersPosts);
		return this.feedPolicy.filter(feedCandidatePosts);
	}

	public List<Post> getUserPosts(String token, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_POSTS));
		
		if (doIsFriend(requester, username) || requester.getUsername().equals(username)) {
			User user = findUserByUsername(username);
			return user.getProfile().getPosts();
		}
		
		return new ArrayList<Post>();
	}
	
	public void deletePost(String token, String postId) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
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
	
	public String login(Map<String, String> credentials) throws AuthenticationException {
		return this.authenticationPlugin.authenticate(credentials);
	}

	private User findUserByUsername(String username) throws UserNotFoundException, InternalErrorException {
		if (admin.getUsername().equals(username)) {
			return admin;
		}
		
		try {
			return this.storageFacade.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			throw new UserNotFoundException(String.format(Messages.Exception.COULD_NOT_FIND_USER, username)); 
		}
	}

	public List<UserSummary> getUserSummaries(String token) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_SUMMARIES));
		
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			if (!user.equals(requester)) {
				UserSummary summary = getUserSummary(requester, user);
				userSummaries.add(summary);
			}
		}

		return userSummaries;
	}

	public List<UserSummary> getUserRecommendations(String token) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_RECOMMENDATIONS));
		
		List<UserSummary> friends = doGetSelfFriends(requester);
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			UserSummary summary = getUserSummary(requester, user);
			
			if (!user.equals(requester) && !friends.contains(summary)
					&& !hasSentFriendshipRequestTo(requester, user.getUsername())
					&& !hasReceivedFriendshipRequestFrom(requester, user.getUsername())) {
				userSummaries.add(summary);
			}
		}
		
		return userSummaries;
	}

	public List<UserSummary> getFollowRecommendations(String token) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(token);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOW_RECOMMENDATIONS));
		
		List<UserSummary> follows = this.doGetFollowedUsers(requester);
		List<UserSummary> userSummaries = new ArrayList<UserSummary>();
		
		for (User user : this.storageFacade.getAllUsers()) {
			UserSummary summary = getUserSummary(requester, user);
			
			if (!user.equals(requester) && !follows.contains(summary)) {
				userSummaries.add(summary);
			}
		}
		
		return userSummaries;
	}

	public boolean isFriend(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.IS_FRIEND));
		return doIsFriend(requester, username);
	}
	
	private boolean doIsFriend(User requester, String username) throws InternalErrorException, MediaNotFoundException, UnauthorizedOperationException {
		List<UserSummary> friends = doGetSelfFriends(requester);
		
		for (UserSummary friend : friends) {
			if (friend.getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;
	}
	
	public UserSummary getSelf(String userToken) throws AuthenticationException, UnauthorizedOperationException, MediaNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_SELF));
		UserSummary summary = getUserSummary(requester, requester);
		return summary;
	}

	public boolean follows(String userToken, String username) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
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

	public void unfollow(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, FollowNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UNFOLLOW));
		
		List<Follow> follows = this.storageFacade.getFollowsByUserId(requester.getUserId());
		
		for (Follow follow : follows) {
			if (follow.getFollowed().getUsername().equals(username)) {
				this.storageFacade.removeFollow(follow);
			}
		}
	}

	public void unfriend(String userToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, FriendshipNotFoundException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UNFRIEND));
		
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

	public void changeSelfProfilePic(String userToken, byte[] picData) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.CHANGE_SELF_PROFILE_PIC));
		doChangeSelfProfilePic(requester, picData);
		this.storageFacade.updateUser(requester);
	}

	private void doChangeSelfProfilePic(User requester, byte[] picData) throws InternalErrorException, UnauthorizedOperationException {
		String picId = createMedia(requester, picData);
		requester.getProfile().setProfilePicId(picId);
	}

	// TODO to be removed
	public byte[] getUserProfilePic(String loggedUserToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(loggedUserToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_USER_PROFILE_PIC));
		return null;
	}

	public void updateProfile(String userToken, String profileDescription, byte[] picData) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.UPDATE_USER_PROFILE));
		requester.getProfile().setDescription(profileDescription);
		doChangeSelfProfilePic(requester, picData);
		this.storageFacade.updateUser(requester);
	}

	public String getMediaUri(String userToken, String mediaId) throws AuthenticationException, MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		User requester = this.authenticationPlugin.getUser(userToken);
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_MEDIA_URI));
		
		if (mediaId.equals(SystemConstants.DEFAULT_PROFILE_PIC_ID)) {
			return SystemConstants.DEFAULT_PROFILE_PIC_PATH;
		}
		
		return mediaStorageFacade.getMediaUri(requester.getUsername(), mediaId);
	}

	private String createMedia(User requester, byte[] mediaData) throws InternalErrorException, UnauthorizedOperationException {
		String mediaId = UUID.randomUUID().toString();
		this.mediaStorageFacade.createMedia(requester.getUsername(), mediaId, new HashMap<String, String>(), mediaData);
		return mediaId;
	}
}
