package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	public void addUser(String userId, String username, String password, String profileDescription) {
		Profile profile = new Profile(profileDescription, new ArrayList<Post>());
		User newUser = new User(username, userId, password, profile);
		this.users.add(newUser);
	}
	
	public void addUser(String username, String password, String profileDescription) {
		UUID uuid = UUID.randomUUID();
		addUser(uuid.toString(), username, password, profileDescription);
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

	public void addFollow(String followerId, String followedId) {
		User follower = findUserById(followerId);
		User followed = findUserById(followedId);
		this.follows.add(new Follow(follower, followed));
	}
	
	public List<User> getFollowedUsers(String followerId) {
		User user = findUserById(followerId);
		List<User> followedUsers = new ArrayList<User>();
		
		for (Follow follow : getFollows()) {
			if (follow.getFollower().equals(user)) {
				followedUsers.add(follow.getFollowed());
			}
		}
		
		return followedUsers;
	}

	public User validateCredentials(String username, String password) {
		User user = findUserByUsername(username);
		
		if (user.getPassword().equals(password)) {
			return user;
		}
		// TODO throw exception
		return null;
	}

	private User findUserByUsername(String username) {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		if (admin.getUsername().equals(username)) {
			return admin;
		}
		// FIXME
		return null;
	}

	public boolean userIsAdmin(String userId) {
		return this.admin.getUserId().equals(userId);
	}

	public List<Post> getFriendsPosts(String userId) {
		List<User> friends = getFriends(userId);
		List<Post> friendsPosts = new ArrayList<Post>();
		
		for (User friend : friends) {
			friendsPosts.addAll(friend.getProfile().getPosts());
		}
		
		return friendsPosts;
	}
}
