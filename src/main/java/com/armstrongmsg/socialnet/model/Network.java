package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;
import java.util.List;

public class Network {
	private Admin admin;
	private List<User> users;
	private List<Group> groups;
	private List<Relationship> relationships;
	
	public Network(Admin admin, List<User> users, List<Group> groups, List<Relationship> relationships) {
		this.admin = admin;
		this.users = users;
		this.groups = groups;
		this.relationships = relationships;
	}

	public Admin getAdmin() {
		return admin;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void addUser(String userId, String username, String profileDescription) {
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		User newUser = new User(username, userId, profile);
		this.users.add(newUser);
	}

	public void removeUser(String userId) {
		User user = findUserById(userId);
		this.users.remove(user);
	}

	private User findUserById(String userId) {
		for (User user : this.users) {
			if (user.getUserId().equals(userId)) {
				return user;
			}
		}
		//FIXME
		return null;
	}
}
