package com.armstrongmsg.socialnet.storage;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.cache.Cache;
import com.armstrongmsg.socialnet.storage.database.DatabaseManager;

public class StorageFacade {
	private Cache cache;
	private DatabaseManager databaseManager;
	
	public StorageFacade(Cache cache, DatabaseManager databaseManager) {
		this.cache = cache;
		this.databaseManager = databaseManager;
	}

	public void saveUser(User user) {
		cache.putUser(user);
		databaseManager.saveUser(user);
	}

	public User getUserById(String userId) {
		User user = cache.getUserById(userId);
		
		if (user == null) {
			user = databaseManager.getUserById(userId);
			cache.putUser(user);
		}
		
		return user;
	}
	
	public User getUserByUsername(String username) {
		User user = cache.getUserByUsername(username);
		
		if (user == null) {
			user = databaseManager.getUserByUsername(username);
			cache.putUser(user);
		}
		
		return user;
	}

	public Group getGroupById(String groupId) {
		Group group = cache.getGroupById(groupId);
		
		if (group == null) {
			group = databaseManager.getGroupById(groupId);
			cache.putGroup(group);
			
		}
		
		return group;
	}
	
	public Group getGroupByName(String groupName) {
		Group group = cache.getGroupById(groupName);
		
		if (group == null) {
			group = databaseManager.getGroupByName(groupName);
			cache.putGroup(group);
		}
		
		return group;
	}
	
	public void saveGroup(Group group) {
		cache.putGroup(group);
		databaseManager.saveGroup(group);
	}
	
	public List<Friendship> getFriendshipsByUserId(String userId) {
		List<Friendship> friendships = cache.getFriendshipsByUserId(userId);
		
		if (friendships == null) {
			friendships = databaseManager.getFriendshipsByUserId(userId);
			cache.putFriendships(friendships);
		}
		
		return friendships;
	}
	
	public List<Friendship> getFriendshipsByUsername(String username) {
		List<Friendship> friendships = cache.getFriendshipsByUserId(username);
		
		if (friendships == null) {
			friendships = databaseManager.getFriendshipsByUsername(username);
			cache.putFriendships(friendships);
		}
		
		return friendships;
	}
	
	public void saveFriendship(Friendship friendship) {
		cache.putFriendship(friendship);
		databaseManager.saveFriendship(friendship);
	}

	public List<Follow> getFollowsByUserId(String userId) {
		List<Follow> follows = cache.getFollowsByUserId(userId);
		
		if (follows == null) {
			follows = databaseManager.getFollowsByUserId(userId);
			cache.putFollows(follows);
		}
		
		return follows;
	}
	
	public List<Follow> getFollowsByUsername(String username) {
		List<Follow> follows = cache.getFollowsByUsername(username);
		
		if (follows == null) {
			follows = databaseManager.getFollowsByUsername(username);
			cache.putFollows(follows);
		}
		
		return follows;
	}
	
	public void saveFollow(Follow follow) {
		cache.putFollow(follow);
		databaseManager.saveFollow(follow);
	}

	public List<User> getAllUsers() {
		return databaseManager.getAllUsers();
	}

	public void removeUserById(String userId) {
		cache.removeUserById(userId);
		databaseManager.removeUserById(userId);
	}
}
