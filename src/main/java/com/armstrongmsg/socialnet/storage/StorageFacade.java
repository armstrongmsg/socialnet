package com.armstrongmsg.socialnet.storage;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
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

	public void updateUser(User user) {
		cache.putUser(user);
		databaseManager.updateUser(user);
	}

	public User getUserById(String userId) throws UserNotFoundException {
		User user = cache.getUserById(userId);
		
		if (user == null) {
			// TODO test
			user = databaseManager.getUserById(userId);
			cache.putUser(user);
		}
		
		return user;
	}
	
	public User getUserByUsername(String username) throws UserNotFoundException {
		User user = cache.getUserByUsername(username);
		
		if (user == null) {
			// TODO test
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
			// TODO test
			friendships = databaseManager.getFriendshipsByUserId(userId);
			cache.putFriendships(friendships);
		}
		
		return friendships;
	}

	public void saveFriendship(Friendship friendship) {
		databaseManager.saveFriendship(friendship);
		cache.putFriendship(friendship);
	}

	public List<Follow> getFollowsByUserId(String userId) {
		List<Follow> follows = cache.getFollowsByUserId(userId);
		
		if (follows == null) {
			// TODO test
			follows = databaseManager.getFollowsByUserId(userId);
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

	public void removeUserById(String userId) throws UserNotFoundException {
		cache.removeUserById(userId);
		databaseManager.removeUserById(userId);
	}

	public void saveFriendshipRequest(FriendshipRequest friendshipRequest) {
		databaseManager.saveFriendshipRequest(friendshipRequest);
		cache.putFriendshipRequest(friendshipRequest);
	}

	public List<FriendshipRequest> getSentFrienshipRequestsById(String userId) {
		List<FriendshipRequest> requests = cache.getSentFriendshipRequestsById(userId);
		
		if (requests == null) {
			requests = databaseManager.getSentFriendshipRequestsById(userId);
			cache.putFriendshipRequests(requests);
		}
		
		return requests;
	}

	public List<FriendshipRequest> getReceivedFrienshipRequestsById(String userId) {
		List<FriendshipRequest> requests = cache.getReceivedFriendshipRequestsById(userId);
		
		if (requests == null) {
			requests = databaseManager.getReceivedFriendshipRequestsById(userId);
			cache.putFriendshipRequests(requests);
		}
		
		return requests;
	}

	public FriendshipRequest getReceivedFrienshipRequestsById(String userId, String username) {
		FriendshipRequest request = cache.getReceivedFriendshipRequestById(userId, username);
		
		if (request == null) {
			request = databaseManager.getReceivedFriendshipRequestById(userId, username);
			cache.putFriendshipRequest(request);
		}
		
		return request;
	}

	public void removeFriendshipRequest(FriendshipRequest friendshipRequest) {
		cache.removeFriendshipRequestById(friendshipRequest);
		databaseManager.removeFriendshipRequestById(friendshipRequest);
	}

	public void removeFollow(Follow follow) {
		cache.removeFollow(follow);
		databaseManager.removeFollow(follow);
	}

	public void removeFriendship(Friendship friendship) {
		cache.removeFriendship(friendship);
		databaseManager.removeFriendship(friendship);
	}
}
