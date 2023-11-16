package com.armstrongmsg.socialnet.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.PropertiesDefaults;
import com.armstrongmsg.socialnet.constants.PropertiesNames;
import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.User;
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
		this.network = new Network(admin, users, groups, friendships, follows);
	}
	
	public static ApplicationFacade getInstance() {
		if (instance == null) {
			instance = new ApplicationFacade();
		}
		
		return instance;
	}
	
	// FIXME to be removed
	public void addUser(String userId, String username, String password, String profileDescription) {
		this.network.addUser(userId, username, profileDescription);
	}
	
	public void addUser(String username, String password, String profileDescription) {
		this.network.addUser(username, password, profileDescription);
	}
	
	public void removeUser(String userId) {
		this.network.removeUser(userId);
	}
	
	public User getAdmin() { 
		return this.network.getAdmin();
	}
	
	public List<User> getUsers() {
		return this.network.getUsers();
	}

	public void createPost(String userId, String title, String content, String postVisibility) {
		this.network.createPost(userId, title, content, postVisibility);
	}

	public List<Post> getUserPosts(String userId) {
		return this.network.getUserPosts(userId);
	}
	
	public void addFriendship(String userId1, String userId2) {
		this.network.addFriendship(userId1, userId2);
	}

	public List<User> getFriends(String userId) {
		return this.network.getFriends(userId);
	}

	public void addFollow(String followerId, String followedId) {
		this.network.addFollow(followerId, followedId);
	}

	public List<User> getFollowedUsers(String userId) {
		return this.network.getFollowedUsers(userId);
	}

	public User validateCredentials(String username, String password) {
		return this.network.validateCredentials(username, password);
	}

	public boolean userIsAdmin(String userId) {
		return this.network.userIsAdmin(userId);
	}

	public List<Post> getFriendsPosts(String userId) {
		return this.network.getFriendsPosts(userId);
	}
}
