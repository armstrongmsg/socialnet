package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.PropertiesDefaults;
import com.armstrongmsg.socialnet.constants.PropertiesNames;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.authentication.AuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.model.authorization.AuthorizationPlugin;
import com.armstrongmsg.socialnet.model.authorization.Operation;
import com.armstrongmsg.socialnet.model.authorization.OperationType;
import com.armstrongmsg.socialnet.model.authorization.OperationOnUser;
import com.armstrongmsg.socialnet.storage.StorageManager;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class Network {
	private Admin admin;
	private List<User> users;
	private List<Group> groups;
	private List<Friendship> friendships;
	private List<Follow> follows;
	private AuthenticationPlugin authenticationPlugin;
	private AuthorizationPlugin authorizationPlugin;
	
	private static Logger logger = LoggerFactory.getLogger(Network.class);
	
	public Network(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships, 
			List<Follow> follows, AuthenticationPlugin authenticationPlugin, AuthorizationPlugin authorizationPlugin) {
		this.admin = admin;
		this.users = users;
		this.groups = groups;
		this.friendships = friendships;
		this.follows = follows;
		this.authenticationPlugin = authenticationPlugin;
		this.authorizationPlugin = authorizationPlugin;
	}

	public Network(StorageManager storageManager) throws FatalErrorException {
		String adminUsername = PropertiesDefaults.DEFAULT_ADMIN_USERNAME;
		String adminPassword = PropertiesDefaults.DEFAULT_ADMIN_PASSWORD;
		
		logger.info(Messages.Logging.LOADING_ADMIN_CONFIGURATION);
		PropertiesUtil properties = PropertiesUtil.getInstance();
		adminUsername = properties.getProperty(PropertiesNames.ADMIN_USERNAME);
		adminPassword = properties.getProperty(PropertiesNames.ADMIN_PASSWORD);
		logger.info(Messages.Logging.LOADED_ADMIN, adminUsername);

		Admin admin = new Admin(adminUsername, adminUsername, adminPassword);
		List<User> users = new ArrayList<User>(storageManager.readUsers());
		List<Group> groups = new ArrayList<Group>(storageManager.readGroups());
		List<Friendship> friendships = new ArrayList<Friendship>(storageManager.readFriendships());
		List<Follow> follows = new ArrayList<Follow>(storageManager.readFollows());
		ClassFactory classFactory = new ClassFactory();
		String authenticationPluginClassName = properties.getProperty(ConfigurationProperties.AUTHENTICATION_PLUGIN_CLASS_NAME);
		String authorizationPluginClassName = properties.getProperty(ConfigurationProperties.AUTHORIZATION_PLUGIN_CLASS_NAME);
		
		// FIXME constant
		logger.info("Loading authentication plugin");
		
		AuthenticationPlugin authenticationPlugin = (AuthenticationPlugin) classFactory.createInstance(authenticationPluginClassName);
		authenticationPlugin.setUp(admin, users);
		
		// FIXME constant
		logger.info("Loading authorization plugin");
		
		AuthorizationPlugin authorizationPlugin = (AuthorizationPlugin) classFactory.createInstance(authorizationPluginClassName);
		authorizationPlugin.setUp(admin, users, groups, friendships, follows);
		
		this.admin = admin;
		this.users = users;
		this.groups = groups;
		this.friendships = friendships;
		this.follows = follows;
		this.authenticationPlugin = authenticationPlugin;
		this.authorizationPlugin = authorizationPlugin;
	}

	public Admin getAdmin() {
		return admin;
	}

	public List<User> getUsers(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_ALL_USERS));
		
		return users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<Friendship> getFriendships() {
		return friendships;
	}

	public List<Follow> getFollows() {
		return follows;
	}

	public void addUser(UserToken userToken, String username, String password, String profileDescription) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_USER));
		
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		UUID uuid = UUID.randomUUID();
		User newUser = new User(uuid.toString(), username, password, profile);
		this.users.add(newUser);
	}
	
	public void addUser(String username, String password, String profileDescription) {
		UUID uuid = UUID.randomUUID();
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		User newUser = new User(uuid.toString(), username, password, profile);
		this.users.add(newUser);
	}

	public void removeUser(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.REMOVE_USER));
		
		User user = findUserById(userId);
		this.users.remove(user);
	}

	private User findUserById(String userId) throws AuthenticationException {
		for (User user : this.users) {
			if (user.getUserId().equals(userId)) {
				return user;
			}
		}
		
		if (admin.getUserId().equals(userId)) {
			return admin;
		}

		// FIXME add message
		throw new AuthenticationException();
	}

	public void createPost(UserToken userToken, String title, String content, PostVisibility newPostVisibility) throws AuthenticationException {
		User user = findUserById(userToken.getUserId());
		user.getProfile().createPost(title, content, newPostVisibility);
	}

	public List<Post> getUserPosts(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		User target = findUserById(userId);
		this.authorizationPlugin.authorize(requester, new OperationOnUser(OperationType.GET_USER_POSTS, target));
		
		User user = findUserById(userId);
		return user.getProfile().getPosts();
	}

	public void addFriendship(UserToken userToken, String userId1, String userId2) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FRIENDSHIP));
		
		User user1 = findUserById(userId1);
		User user2 = findUserById(userId2);
		this.friendships.add(new Friendship(user1, user2));
	}

	public List<User> getFriends(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS));
		User user = findUserById(userId);
		List<User> friends = new ArrayList<User>();
		
		for (Friendship friendship : getFriendships()) {
			if (friendship.getFriend1().equals(user)) {
				friends.add(friendship.getFriend2());
			}
			
			if (friendship.getFriend2().equals(user)) {
				friends.add(friendship.getFriend1());
			}
		}
		
		return friends;
	}

	public void addFollow(UserToken userToken, String followerId, String followedId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.ADD_FOLLOW));
		
		User follower = findUserById(followerId);
		User followed = findUserById(followedId);
		this.follows.add(new Follow(follower, followed));
	}
	
	public List<User> getFollowedUsers(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(userToken.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FOLLOWED_USERS));
		
		User user = findUserById(userToken.getUserId());
		List<User> followedUsers = new ArrayList<User>();
		
		for (Follow follow : getFollows()) {
			if (follow.getFollower().equals(user)) {
				followedUsers.add(follow.getFollowed());
			}
		}
		
		return followedUsers;
	}

	public boolean userIsAdmin(String userId) {
		return this.admin.getUserId().equals(userId);
	}

	public List<Post> getFriendsPosts(UserToken token, String userId) throws UnauthorizedOperationException, AuthenticationException {
		User requester = findUserById(token.getUserId());
		this.authorizationPlugin.authorize(requester, new Operation(OperationType.GET_FRIENDS_POSTS));
		User user = findUserById(userId);
		List<User> friends = new ArrayList<User>();
		
		for (Friendship friendship : getFriendships()) {
			if (friendship.getFriend1().equals(user)) {
				friends.add(friendship.getFriend2());
			}
			
			if (friendship.getFriend2().equals(user)) {
				friends.add(friendship.getFriend1());
			}
		}
		
		List<Post> friendsPosts = new ArrayList<Post>();
		
		for (User friend : friends) {
			friendsPosts.addAll(friend.getProfile().getPosts());
		}
		
		return friendsPosts;
	}

	public UserToken login(Map<String, String> credentials) throws AuthenticationException {
		return this.authenticationPlugin.authenticate(credentials);
	}
}
