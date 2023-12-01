package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public class DefaultCache implements Cache {
	private List<User> users;
	private List<Friendship> friendships;
	private List<Follow> follows;

	public DefaultCache() {
		this.users = new ArrayList<User>();
		this.friendships = new ArrayList<Friendship>();
		this.follows = new ArrayList<Follow>();
	}
	
	@Override
	public User getUserById(String id) {
		for (User user : this.users) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}

		return null;
	}

	@Override
	public User getUserByUsername(String username) {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		return null;
	}

	@Override
	public void putUser(User user) {
		this.users.add(user);
	}

	@Override
	public Group getGroupById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group getGroupByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putGroup(Group group) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		List<Friendship> friendships = new ArrayList<Friendship>();
		
		for (Friendship friendship : this.friendships) {
			if (friendship.getFriend1().getUserId().equals(userId)) {
				friendships.add(friendship);
			}
			
			if (friendship.getFriend2().getUserId().equals(userId)) {
				friendships.add(friendship);
			}
		}
		
		return friendships;
	}

	@Override
	public List<Friendship> getFriendshipsByUsername(String username) {
		List<Friendship> friendships = new ArrayList<Friendship>();
		
		for (Friendship friendship : this.friendships) {
			if (friendship.getFriend1().getUsername().equals(username)) {
				friendships.add(friendship);
			}
			
			if (friendship.getFriend2().getUsername().equals(username)) {
				friendships.add(friendship);
			}
		}
		
		return friendships;
	}

	@Override
	public void putFriendship(Friendship friendship) {
		this.friendships.add(friendship);
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
		this.friendships.addAll(friendships);
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		List<Follow> follows = new ArrayList<Follow>();
		
		for (Follow follow : this.follows) {
			if (follow.getFollowed().getUserId().equals(userId)) {
				follows.add(follow);
			}
			
			if (follow.getFollower().getUserId().equals(userId)) {
				follows.add(follow);
			}
		}
		
		return follows;
	}

	@Override
	public List<Follow> getFollowsByUsername(String username) {
		List<Follow> follows = new ArrayList<Follow>();
		
		for (Follow follow : this.follows) {
			if (follow.getFollowed().getUsername().equals(username)) {
				follows.add(follow);
			}
			
			if (follow.getFollower().getUsername().equals(username)) {
				follows.add(follow);
			}
		}
		
		return follows;
	}

	@Override
	public void putFollow(Follow follow) {
		this.follows.add(follow);
	}

	@Override
	public void putFollows(List<Follow> follows) {
		this.follows.addAll(follows);
	}

	public List<User> getUsers() {
		return users;
	}

	@Override
	public void removeUserById(String userId) {
		User userToRemove = getUserById(userId);
		this.users.remove(userToRemove);
	}
}
