package com.armstrongmsg.socialnet.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.storage.StorageManager;

public class ApplicationFacade {
	private static Logger logger = LoggerFactory.getLogger(ApplicationFacade.class);
	private static ApplicationFacade instance;

	private Network network;
	private StorageManager storageManager;
	
	private ApplicationFacade(StorageManager storageManager) {
		this.storageManager = storageManager;

		try {
			this.network = new Network(this.storageManager);
		} catch (FatalErrorException e) {
			// TODO how to handle this properly?
			logger.error(e.getMessage());
		}
	}
	
	public static ApplicationFacade getInstance(StorageManager storageManager) {
		if (instance == null) {
			instance = new ApplicationFacade(storageManager);
		}
		
		return instance;
	}
	
	public static ApplicationFacade getInstance() {
		return getInstance(new StorageManager());
	}
		
	public static void reset() {
		instance = null;
	}
	
	public void addUser(UserToken userToken, String username, String password, String profileDescription) throws UnauthorizedOperationException, AuthenticationException {
		this.network.addUser(userToken, username, password, profileDescription);
	}
	
	public void addUser(String username, String password, String profileDescription) {
		this.network.addUser(username, password, profileDescription);
	}
	
	public void removeUser(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		this.network.removeUser(userToken, userId);
	}

	public List<User> getUsers(UserToken userToken) throws UnauthorizedOperationException, AuthenticationException {
		return this.network.getUsers(userToken);
	}

	public void createPost(UserToken userToken, String title, String content, PostVisibility newPostVisibility) throws AuthenticationException {
		this.network.createPost(userToken, title, content, newPostVisibility);
	}

	public List<Post> getUserPosts(UserToken userToken, String userId) throws UnauthorizedOperationException, AuthenticationException {
		return this.network.getUserPosts(userToken, userId);
	}
	
	public void addFriendship(UserToken userToken, String userId1, String userId2) {
		try {
			this.network.addFriendship(userToken, userId1, userId2);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<User> getFriends(UserToken userToken, String userId) {
		try {
			return this.network.getFriends(userToken, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public void addFollow(UserToken userToken, String followerId, String followedId) {
		try {
			this.network.addFollow(userToken, followerId, followedId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<User> getFollowedUsers(UserToken userToken, String userId) {
		try {
			return this.network.getFollowedUsers(userToken, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public UserToken login(Map<String, String> credentials) throws AuthenticationException {
		return this.network.login(credentials);
	}

	public boolean userIsAdmin(String userId) {
		return this.network.userIsAdmin(userId);
	}

	public List<Post> getFriendsPosts(UserToken token, String userId) {
		try {
			return this.network.getFriendsPosts(token, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
