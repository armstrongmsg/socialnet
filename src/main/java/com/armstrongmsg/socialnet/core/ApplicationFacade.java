package com.armstrongmsg.socialnet.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.InvalidParameterException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.UserSummary;
import com.armstrongmsg.socialnet.storage.MediaStorageFacade;
import com.armstrongmsg.socialnet.storage.StorageFacade;
import com.armstrongmsg.socialnet.storage.cache.Cache;
import com.armstrongmsg.socialnet.storage.cache.CacheFactory;
import com.armstrongmsg.socialnet.storage.database.DatabaseManager;
import com.armstrongmsg.socialnet.storage.database.DatabaseManagerFactory;
import com.armstrongmsg.socialnet.storage.media.MediaRepository;
import com.armstrongmsg.socialnet.storage.media.MediaRepositoryFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class ApplicationFacade {
	private static Logger logger = LoggerFactory.getLogger(ApplicationFacade.class);
	private static ApplicationFacade instance;

	private Network network;
	private StorageFacade storageManager;
	private MediaStorageFacade mediaStorageFacade;
	
	private ApplicationFacade(StorageFacade storageManager, MediaStorageFacade mediaStorageFacade) {
		this.storageManager = storageManager;
		this.mediaStorageFacade = mediaStorageFacade;

		try {
			this.network = new Network(this.storageManager, this.mediaStorageFacade);
			PropertiesUtil properties = PropertiesUtil.getInstance();
			if (properties.getProperty(ConfigurationProperties.BOOTSTRAP).equals(SystemConstants.PROPERTY_VALUE_TRUE)) {
				logger.info("Starting network with bootstrap.");
				new Bootstrap().startNetwork(network);
			}
		} catch (FatalErrorException e) {
			logger.error(e.getMessage());
		}
	}
	
	public synchronized static ApplicationFacade getInstance(StorageFacade storageFacade, 
			MediaStorageFacade mediaStorageFacade) {
		if (instance == null) {
			instance = new ApplicationFacade(storageFacade, mediaStorageFacade);
		}
		
		return instance;
	}
	
	public synchronized static ApplicationFacade getInstance() {
		if (instance == null) {
			try {
				Cache cache = new CacheFactory().loadCacheFromConfiguration();
				DatabaseManager dbManager = new DatabaseManagerFactory().loadDatabaseManagerFromConfiguration();
				MediaRepository mediaRepo = new MediaRepositoryFactory().loadMediaRepositoryFromConfiguration();
				StorageFacade storageFacade = new StorageFacade(cache, dbManager); 
				MediaStorageFacade mediaStorageFacade = new MediaStorageFacade(mediaRepo);
				return getInstance(storageFacade, mediaStorageFacade);
			} catch (FatalErrorException e) {
				logger.error(e.getMessage());
				return null;
			}			
		} else {
			return instance;
		}
	}
		
	public synchronized static void reset() {
		instance = null;
	}

	public void shutdown() {
		try {
			this.storageManager.shutdown();
		} catch (InternalErrorException e) {
			// TODO think on how to treat this exception
		}
	}
	
	public String login(Map<String, String> credentials) throws AuthenticationException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_LOGIN_REQUEST);
		return this.network.login(credentials);
	}

	public boolean userIsAdmin(String userId) throws UserNotFoundException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_USER_IS_ADMIN_REQUEST, userId);
		return this.network.userIsAdmin(userId);
	}
	
	// ADMIN ONLY OPERATIONS
	
	public void addUser(String userToken, String username, String password, String profileDescription)
			throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, UserAlreadyExistsException {
		logger.debug(Messages.Logging.RECEIVED_ADD_USER_ADMIN_REQUEST, userToken, username, password, profileDescription);
		this.network.addUser(userToken, username, password, profileDescription);
	}
	
	public void removeUser(String userToken, String userId) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_REMOVE_USER_ADMIN_REQUEST, userToken, userId);
		this.network.removeUser(userToken, userId);
	}
	
	public List<User> getUsers(String userToken) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_GET_USERS_ADMIN_REQUEST, userToken);
		return this.network.getUsers(userToken);
	}

	public List<Post> getUserPostsAdmin(String userToken, String userId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_GET_USERS_POSTS_ADMIN_REQUEST, userToken, userId);
		return this.network.getUserPostsAdmin(userToken, userId);
	}
	
	public void addFriendshipAdmin(String userToken, String userId1, String userId2) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, FriendshipAlreadyExistsException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FRIENDSHIP_ADMIN_REQUEST, userToken, userId1, userId2);
		this.network.addFriendshipAdmin(userToken, userId1, userId2);
	}
	
	public List<User> getFriends(String userToken, String userId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_GET_FRIENDS_ADMIN_REQUEST, userToken, userId);
		return this.network.getFriends(userToken, userId);
	}
	
	public void addFollowAdmin(String userToken, String followerId, String followedId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, FollowAlreadyExistsException {
		logger.debug(Messages.Logging.RECEIVED_ADD_FOLLOW_ADMIN_REQUEST, userToken, followerId, followedId);
		this.network.addFollowAdmin(userToken, followerId, followedId);
	}
	
	public List<User> getFollowedUsers(String userToken, String userId) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_GET_FOLLOWED_USERS_ADMIN_REQUEST, userToken, userId);
		return this.network.getFollowedUsers(userToken, userId);
	}
	
	// REGULAR USER OPERATIONS
	
	public void addUser(String username, String password, String profileDescription)
			throws InternalErrorException, InvalidParameterException, UserAlreadyExistsException {
		logger.debug(Messages.Logging.RECEIVED_ADD_USER_REQUEST, username, password, profileDescription);
		
		try {
			this.network.addUser(username, password, profileDescription);
		} catch (InternalErrorException e) {
			logger.debug(Messages.Logging.INTERNAL_ERROR_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public UserSummary getSelf(String userToken) throws AuthenticationException, InternalErrorException {
		logger.debug(Messages.Logging.RECEIVED_GET_SELF_REQUEST, userToken);
		
		try {
			return this.network.getSelf(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (InternalErrorException e) {
			logger.debug(Messages.Logging.INTERNAL_ERROR_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public void createPost(String userToken, String title, String content, PostVisibility newPostVisibility, List<byte[]> postMediaData)
			throws AuthenticationException, InternalErrorException, InvalidParameterException {
		logger.debug(Messages.Logging.RECEIVED_CREATE_POST_REQUEST, userToken, title, content, newPostVisibility);
		
		try {
			this.network.createPost(userToken, title, content, newPostVisibility, postMediaData);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (InternalErrorException e) {
			logger.debug(Messages.Logging.INTERNAL_ERROR_EXCEPTION, e.getMessage());
			throw e;
		} catch (InvalidParameterException e) {
			logger.debug(Messages.Logging.INVALID_PARAMETER_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public List<Post> getSelfPosts(String userToken) throws UnauthorizedOperationException, AuthenticationException {
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

	public List<Post> getUserPosts(String userToken, String username) 
			throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException, MediaNotFoundException {
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
	
	public void addFriendshipRequest(String userToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FriendshipRequestAlreadyExistsException, InternalErrorException {
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
	
	public List<FriendshipRequest> getSentFriendshipRequests(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException {
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
	
	public List<FriendshipRequest> getReceivedFriendshipRequests(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException {
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
	
	public void acceptFriendshipRequest(String userToken, String username) 
			throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FriendshipRequestNotFound, 
			InternalErrorException, FriendshipAlreadyExistsException {
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
	
	public void rejectFriendshipRequest(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, 
		FriendshipRequestNotFound, InternalErrorException {
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
	
	public void addFriendship(String userToken, String username) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, 
		InternalErrorException, FriendshipAlreadyExistsException {
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
	
	public List<UserSummary> getSelfFriends(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
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

	public List<Post> getFriendsPosts(String token) throws UnauthorizedOperationException, AuthenticationException, UserNotFoundException, InternalErrorException {
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
	
	public List<Post> getFeedPosts(String token) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
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

	public void deletePost(String token, String postId) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException {
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

	public void addFollow(String userToken, String followedUsername) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, FollowAlreadyExistsException, InternalErrorException {
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

	public List<UserSummary> getFollowedUsers(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
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

	public List<UserSummary> getUserSummaries(String userToken) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
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

	public List<UserSummary> getUserRecommendations(String userToken) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
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

	public List<UserSummary> getFollowRecommendations(String userToken) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
		logger.debug(Messages.Logging.RECEIVED_GET_FOLLOW_RECOMMENDATIONS_REQUEST, userToken);

		try {
			return this.network.getFollowRecommendations(userToken);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	public boolean isFriend(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, MediaNotFoundException {
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

	public boolean follows(String userToken, String username) throws UnauthorizedOperationException, AuthenticationException, InternalErrorException, MediaNotFoundException {
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

	public void unfollow(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, FollowNotFoundException {
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

	public void unfriend(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, InternalErrorException, FriendshipNotFoundException {
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
	
	public void changeSelfProfilePic(String userToken, byte[] picData) throws AuthenticationException, InternalErrorException, InvalidParameterException {
		logger.debug(Messages.Logging.RECEIVED_CHANGE_PROFILE_PIC_REQUEST, userToken);

		try {
			this.network.changeSelfProfilePic(userToken, picData);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (InvalidParameterException e) {
			logger.debug(Messages.Logging.INVALID_PARAMETER_EXCEPTION, e.getMessage());
			throw e;
		} catch (InternalErrorException e) {
			logger.debug(Messages.Logging.INTERNAL_ERROR_EXCEPTION, e.getMessage());
			throw e;
		}
	}

	// TODO to be removed
	public byte[] getUserPic(String userToken, String username) throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException, InternalErrorException{
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

	public void updateProfile(String userToken, String profileDescription, byte[] picData) 
			throws AuthenticationException, InternalErrorException, InvalidParameterException {
		logger.debug(Messages.Logging.RECEIVED_UPDATE_USER_PROFILE, userToken);
		
		try {
			this.network.updateProfile(userToken, profileDescription, picData);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (InternalErrorException e) {
			logger.debug(Messages.Logging.INTERNAL_ERROR_EXCEPTION, e.getMessage());
			throw e;
		} catch (InvalidParameterException e) { 
			logger.debug(Messages.Logging.INVALID_PARAMETER_EXCEPTION, e.getMessage());
			throw e;
		}
	}
	
	public String getMediaUri(String userToken, String mediaId) throws AuthenticationException, MediaNotFoundException,
			InternalErrorException, UnauthorizedOperationException {
		logger.debug(Messages.Logging.RECEIVED_GET_MEDIA_URI, userToken, mediaId);
		
		try {
			return this.network.getMediaUri(userToken, mediaId);
		} catch (AuthenticationException e) {
			logger.debug(Messages.Logging.AUTHENTICATION_EXCEPTION, e.getMessage());
			throw e;
		} catch (UnauthorizedOperationException e) {
			logger.debug(Messages.Logging.AUTHORIZATION_EXCEPTION, e.getMessage());
			throw e;
		}
	}
}
