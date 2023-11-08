package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;
import java.util.List;

public class Network {
	private Admin admin;
	private List<User> users;
	private List<Group> groups;
	private List<Friendship> friendships;
	private List<Follow> follows;
	
	public Network(Admin admin, List<User> users, List<Group> groups, List<Friendship> friendships, List<Follow> follows) {
		this.admin = admin;
		this.users = users;
		this.groups = groups;
		this.friendships = friendships;
		this.follows = follows;
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

	public List<Friendship> getFriendships() {
		return friendships;
	}

	public List<Follow> getFollows() {
		return follows;
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

	public void createPost(String userId, String title, String content, String postVisibility) {
		User user = findUserById(userId);
		user.getProfile().createPost(title, content, postVisibility);
	}

	public List<Post> getUserPosts(String userId) {
		User user = findUserById(userId);
		return user.getProfile().getPosts();
	}

	public void addFriendship(String userId1, String userId2) {
		User user1 = findUserById(userId1);
		User user2 = findUserById(userId2);
		this.friendships.add(new Friendship(user1, user2));
	}

	public List<User> getFriends(String userId) {
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
}
