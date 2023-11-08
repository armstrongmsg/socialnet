package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.User;

public class ApplicationFacade {
	private static ApplicationFacade instance;
	
	private Network network;
	
	private ApplicationFacade() {
		Admin admin = new Admin("admin", "admin");
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
	
	public void addUser(String userId, String username, String profileDescription) {
		this.network.addUser(userId, username, profileDescription);
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
}
