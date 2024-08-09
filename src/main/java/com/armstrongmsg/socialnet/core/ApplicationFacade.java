package com.armstrongmsg.socialnet.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.storage.cache.CacheFactory;
import com.armstrongmsg.socialnet.storage.database.DatabaseManagerFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO test authentication and authorization exceptions
public class ApplicationFacade {
	private static Logger logger = LoggerFactory.getLogger(ApplicationFacade.class);
	private static ApplicationFacade instance;

	private Network network;
	private StorageFacade storageManager;
	
	private ApplicationFacade(StorageFacade storageManager) {
		this.storageManager = storageManager;

		try {
			// TODO the network startup should be done on application startup
			this.network = new Network(this.storageManager);
			PropertiesUtil properties = PropertiesUtil.getInstance();
			if (properties.getProperty(ConfigurationProperties.BOOTSTRAP).equals(SystemConstants.PROPERTY_VALUE_TRUE)) {
				logger.info("Starting network with bootstrap.");
				new Bootstrap().startNetwork(network);
			}
		} catch (FatalErrorException e) {
			logger.error(e.getMessage());
		}
	}
	
	public synchronized static ApplicationFacade getInstance(StorageFacade storageFacade) {
		if (instance == null) {
			instance = new ApplicationFacade(storageFacade);
		}
		
		return instance;
	}
	
	public synchronized static ApplicationFacade getInstance() {
		try {
			return getInstance(new StorageFacade(
					new CacheFactory().loadCacheFromConfiguration(),
					new DatabaseManagerFactory().loadDatabaseManagerFromConfiguration()));
		} catch (FatalErrorException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
		
	public synchronized static void reset() {
		instance = null;
	}
	
	public UserToken login(Map<String, String> credentials) throws AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_LOGIN_REQUEST);
		return this.network.login(credentials);
	}

	public boolean userIsAdmin(String userId) {
		logger.debug(Messages.Logging.RECEIVED_USER_IS_ADMIN_REQUEST, userId);
		return this.network.userIsAdmin(userId);
	}
	
	// ADMIN ONLY OPERATIONS
	
	public void addUser(UserToken userToken, String username, String password, String profileDescription) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_ADD_USER_ADMIN_REQUEST, userToken, username, password, profileDescription);
		this.network.addUser(userToken, username, password, profileDescription);
	}
	
	public void removeUser(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_REMOVE_USER_ADMIN_REQUEST, userToken, userId);
		this.network.removeUser(userToken, userId);
	}
	
	public List<User> getUsers(UserToken userToken) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_GET_USERS_ADMIN_REQUEST, userToken);
		return this.network.getUsers(userToken);
	}

	public List<Post> getUserPostsAdmin(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_USERS_POSTS_ADMIN_REQUEST, userToken, userId);
		return this.network.getUserPostsAdmin(userToken, userId);
	}
	
	public void addFriendshipAdmin(UserToken userToken, String userId1, String userId2) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FRIENDSHIP_ADMIN_REQUEST, userToken, userId1, userId2);
		this.network.addFriendshipAdmin(userToken, userId1, userId2);
	}
	
	public List<User> getFriends(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_FRIENDS_ADMIN_REQUEST, userToken, userId);
		return this.network.getFriends(userToken, userId);
	}
	
	public void addFollowAdmin(UserToken userToken, String followerId, String followedId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FOLLOW_ADMIN_REQUEST, userToken, followerId, followedId);
		this.network.addFollowAdmin(userToken, followerId, followedId);
	}
	
	public List<User> getFollowedUsers(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_FOLLOWED_USERS_ADMIN_REQUEST, userToken, userId);
		return this.network.getFollowedUsers(userToken, userId);
	}
	
	// REGULAR USER OPERATIONS
	
	public void addUser(String username, String password, String profileDescription) {
		logger.debug(Messages.Logging.RECEIVED_ADD_USER_REQUEST, username, password, profileDescription);
		this.network.addUser(username, password, profileDescription);
	}

	public void createPost(UserToken userToken, String title, String content, PostVisibility newPostVisibility, byte[] pictureData) throws AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_CREATE_POST_REQUEST, userToken, title, content, newPostVisibility);
		
		try {
			this.network.createPost(userToken, title, content, newPostVisibility, pictureData);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<Post> getSelfPosts(UserToken userToken) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_GET_SELF_POSTS_REQUEST, userToken);
		
		try {
			return this.network.getSelfPosts(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<Post> getUserPosts(UserToken userToken, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_USER_POSTS_REQUEST, userToken, username);
		
		try {
			return this.network.getUserPosts(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void addFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FRIENDSHIP_REQUEST, userToken, username);
		
		try {
			this.network.addFriendshipRequest(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public List<FriendshipRequest> getSentFriendshipRequests(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_SENT_FRIENDSHIP_REQUESTS_REQUEST, userToken);
		
		try {
			return this.network.getSentFriendshipRequests(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public List<FriendshipRequest> getReceivedFriendshipRequests(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_RECEIVED_FRIENDSHIP_REQUESTS_REQUEST, userToken);
		
		try {
			return this.network.getReceivedFriendshipRequests(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void acceptFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ACCEPT_FRIENDSHIP_REQUEST, userToken, username);
		
		try {
			this.network.acceptFriendshipRequest(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void rejectFriendshipRequest(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_REJECT_FRIENDSHIP_REQUEST, userToken, username);
		
		try {
			this.network.rejectFriendshipRequest(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void addFriendship(UserToken userToken, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FRIENDSHIP_REQUEST, userToken, username);
		
		try {
			this.network.addFriendship(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public List<UserSummary> getSelfFriends(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_SELF_FRIENDS_REQUEST, userToken);
		
		try {
			return this.network.getSelfFriends(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<Post> getFriendsPosts(UserToken token) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_FRIENDS_POSTS_REQUEST, token);
		
		try {
			return this.network.getFriendsPosts(token);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public List<Post> getFeedPosts(UserToken token) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_FEED_POSTS_REQUEST, token);

		try {
			return this.network.getFeedPosts(token);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public void deletePost(UserToken token, String postId) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_DELETE_POST_REQUEST, token, postId);
		
		try {
			this.network.deletePost(token, postId);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public void addFollow(UserToken userToken, String followedUsername) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FOLLOW_REQUEST, userToken, followedUsername);
		
		try {
			this.network.addFollow(userToken, followedUsername);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<UserSummary> getFollowedUsers(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_FOLLOWED_USERS_REQUEST, userToken);
		
		try {
			return this.network.getFollowedUsers(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<UserSummary> getUserSummaries(UserToken userToken) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_GET_USER_SUMMARIES_REQUEST, userToken);
		
		try {
			return this.network.getUserSummaries(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<UserSummary> getUserRecommendations(UserToken userToken) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_GET_USER_RECOMMENDATIONS_REQUEST, userToken);
		
		try {
			return this.network.getUserRecommendations(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public boolean isFriend(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_IS_FRIEND_REQUEST, userToken, username);
		
		try {
			return this.network.isFriend(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public UserSummary getSelf(UserToken userToken) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_SELF_REQUEST, userToken);
		
		try {
			return this.network.getSelf(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public boolean follows(UserToken userToken, String username) throws UnauthorizedOperationException, AuthenticationException {
		logger.debug(Messages.Logging.RECEIVED_FOLLOWS_REQUEST, userToken, username);
		
		try {
			return this.network.follows(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public void unfollow(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_UNFOLLOW_REQUEST, userToken, username);
		
		try {
			this.network.unfollow(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public void unfriend(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_UNFRIEND_REQUEST, userToken, username);
		
		try {
			this.network.unfriend(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void changeSelfProfilePic(UserToken userToken, byte[] picData) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_CHANGE_PROFILE_PIC_REQUEST, userToken);

		try {
			this.network.changeSelfProfilePic(userToken, picData);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public byte[] getUserPic(UserToken userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException{
		logger.debug(Messages.Logging.RECEIVED_GET_USER_PROFILE_PIC, userToken);
		
		try {
			return this.network.getUserProfilePic(userToken, username);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UserNotFoundException e) {
			logger.debug(Messages.Logging.USER_NOT_FOUND_EXCEPTION, e.getMessage());
			throw e;
		}
	}
}
