package com.armstrongmsg.socialnet.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.PropertiesDefaults;
import com.armstrongmsg.socialnet.constants.PropertiesNames;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.model.authentication.AuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.LocalPasswordBasedAuthenticationPlugin;
import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.model.authorization.AdminAuthorizationPlugin;
import com.armstrongmsg.socialnet.model.authorization.AuthorizationPlugin;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class ApplicationFacade {
	private static Logger logger = LoggerFactory.getLogger(ApplicationFacade.class);
	private static ApplicationFacade instance;

	private Network network;
	
	private ApplicationFacade() {
		String adminUsername = PropertiesDefaults.DEFAULT_ADMIN_USERNAME;
		String adminPassword = PropertiesDefaults.DEFAULT_ADMIN_PASSWORD;
		
		try {
			logger.info(Messages.Logging.LOADING_ADMIN_CONFIGURATION);
			PropertiesUtil properties = new PropertiesUtil();
			adminUsername = properties.getProperty(PropertiesNames.ADMIN_USERNAME);
			adminPassword = properties.getProperty(PropertiesNames.ADMIN_PASSWORD);
			logger.info(Messages.Logging.LOADED_ADMIN, adminUsername);
		} catch (FileNotFoundException e) {
			logger.error(Messages.Logging.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage());
		} catch (IOException e) {
			logger.error(Messages.Logging.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage());
		}

		Admin admin = new Admin(adminUsername, adminUsername, adminPassword);
		List<User> users = new ArrayList<User>();
		List<Group> groups = new ArrayList<Group>();
		List<Friendship> friendships = new ArrayList<Friendship>();
		List<Follow> follows = new ArrayList<Follow>();
		AuthenticationPlugin authenticationPlugin = new LocalPasswordBasedAuthenticationPlugin(users, admin); 
		AuthorizationPlugin authorizationPlugin = new AdminAuthorizationPlugin(admin);
		this.network = new Network(admin, users, groups, friendships, follows, authenticationPlugin, authorizationPlugin);
	}
	
	public static ApplicationFacade getInstance() {
		if (instance == null) {
			instance = new ApplicationFacade();
		}
		
		return instance;
	}
	
	public void addUser(UserToken userToken, String username, String password, String profileDescription) {
		try {
			this.network.addUser(userToken, username, password, profileDescription);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}
	
	public void addUser(String username, String password, String profileDescription) {
		try {
			this.network.addUser(username, password, profileDescription);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}
	
	public void removeUser(UserToken userToken, String userId) {
		try {
			this.network.removeUser(userToken, userId);
		
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}
	
	public User getAdmin() { 
		return this.network.getAdmin();
	}
	
	public List<User> getUsers() {
		return this.network.getUsers();
	}

	public void createPost(UserToken userToken, String title, String content, String postVisibility) {
		try {
			this.network.createPost(userToken, title, content, postVisibility);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}

	public List<Post> getUserPosts(UserToken userToken, String userId) {
		try {
			return this.network.getUserPosts(userToken, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
		return null;
	}
	
	public void addFriendship(UserToken userToken, String userId1, String userId2) {
		try {
			this.network.addFriendship(userToken, userId1, userId2);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}

	public List<User> getFriends(UserToken userToken, String userId) {
		try {
			return this.network.getFriends(userToken, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
		
		return null;
	}

	public void addFollow(UserToken userToken, String followerId, String followedId) {
		try {
			this.network.addFollow(userToken, followerId, followedId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
	}

	public List<User> getFollowedUsers(UserToken userToken, String userId) {
		try {
			return this.network.getFollowedUsers(userToken, userId);
		} catch (UnauthorizedOperationException e) {
			// FIXME treat exception
		}
		
		return null;
	}

	public UserToken login(Map<String, String> credentials) {
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
		}
		
		return null;
	}
}
