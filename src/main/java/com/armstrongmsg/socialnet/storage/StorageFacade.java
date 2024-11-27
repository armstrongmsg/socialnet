package com.armstrongmsg.socialnet.storage;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
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
	
	public void shutdown() throws InternalErrorException {
		this.cache.shutdown();
		this.databaseManager.shutdown();
	}

	public void saveUser(User user) throws InternalErrorException, UserAlreadyExistsException {
		cache.putUser(user);
		databaseManager.saveUser(user);
	}

	public void updateUser(User user) throws UserNotFoundException, InternalErrorException {
		cache.updateUser(user);
		databaseManager.updateUser(user);
	}

	public User getUserById(String userId) throws UserNotFoundException, InternalErrorException {
		try {
			return cache.getUserById(userId);
		} catch (UserNotFoundException e) {
			User user = databaseManager.getUserById(userId);
			try {
				cache.putUser(user);
			} catch (UserAlreadyExistsException e1) {
				// should never occur
			}
			
			return user;
		}
	}
	
	public User getUserByUsername(String username) throws UserNotFoundException, InternalErrorException {
		try {
			return cache.getUserByUsername(username);
		} catch (UserNotFoundException e) {
			User user = databaseManager.getUserByUsername(username);
			try {
				cache.putUser(user);
			} catch (UserAlreadyExistsException e1) {
				// should never occur
			}

			return user;
		}
	}
	
	public List<Friendship> getFriendshipsByUserId(String userId) throws InternalErrorException {
		List<Friendship> friendships = cache.getFriendshipsByUserId(userId);
		
		// FIXME should catch an exception by the cache
		if (friendships.isEmpty()) {
			friendships = databaseManager.getFriendshipsByUserId(userId);
			cache.putFriendships(friendships);
		}
		
		return friendships;
	}

	public void saveFriendship(Friendship friendship) 
			throws InternalErrorException, FriendshipAlreadyExistsException {
		databaseManager.saveFriendship(friendship);
		cache.putFriendship(friendship);
	}

	public List<Follow> getFollowsByUserId(String userId) throws InternalErrorException {
		List<Follow> follows = cache.getFollowsByUserId(userId);
		
		// FIXME should catch an exception by the cache
		if (follows.isEmpty()) {
			follows = databaseManager.getFollowsByUserId(userId);
			cache.putFollows(follows);
		}
		
		return follows;
	}
	
	public void saveFollow(Follow follow) throws FollowAlreadyExistsException, InternalErrorException {
		cache.putFollow(follow);
		databaseManager.saveFollow(follow);
	}

	public List<User> getAllUsers() throws InternalErrorException {
		return databaseManager.getAllUsers();
	}

	public void removeUserById(String userId) throws UserNotFoundException, InternalErrorException {
		cache.removeUserById(userId);
		databaseManager.removeUserById(userId);
	}

	public void saveFriendshipRequest(FriendshipRequest friendshipRequest) 
			throws FriendshipRequestAlreadyExistsException, InternalErrorException {
		databaseManager.saveFriendshipRequest(friendshipRequest);
		cache.putFriendshipRequest(friendshipRequest);
	}

	public List<FriendshipRequest> getSentFrienshipRequestsById(String userId) throws InternalErrorException {
		List<FriendshipRequest> requests = cache.getSentFriendshipRequestsById(userId);
		
		// FIXME should catch an exception by the cache
		if (requests.isEmpty()) {
			requests = databaseManager.getSentFriendshipRequestsById(userId);
			cache.putFriendshipRequests(requests);
		}
		
		return requests;
	}

	public List<FriendshipRequest> getReceivedFrienshipRequestsById(String userId) throws InternalErrorException {
		List<FriendshipRequest> requests = cache.getReceivedFriendshipRequestsById(userId);
		
		// FIXME should catch an exception by the cache
		if (requests.isEmpty()) {
			requests = databaseManager.getReceivedFriendshipRequestsById(userId);
			cache.putFriendshipRequests(requests);
		}
		
		return requests;
	}

	public FriendshipRequest getReceivedFrienshipRequestsById(String userId, String username) 
			throws FriendshipRequestNotFound, InternalErrorException {
		try {
			return cache.getReceivedFriendshipRequestById(userId, username);
		} catch (FriendshipRequestNotFound e) {
			FriendshipRequest request = databaseManager.getReceivedFriendshipRequestById(userId, username);
			
			try {
				cache.putFriendshipRequest(request);
			} catch (FriendshipRequestAlreadyExistsException e1) {
				// should never occur
			}
			
			return request;
		}
	}

	public void removeFriendshipRequest(FriendshipRequest friendshipRequest) 
			throws FriendshipRequestNotFound, InternalErrorException{
		cache.removeFriendshipRequestById(friendshipRequest);
		databaseManager.removeFriendshipRequestById(friendshipRequest);
	}

	public void removeFollow(Follow follow) throws FollowNotFoundException, InternalErrorException {
		cache.removeFollow(follow);
		databaseManager.removeFollow(follow);
	}

	public void removeFriendship(Friendship friendship) 
			throws FriendshipNotFoundException, InternalErrorException {
		cache.removeFriendship(friendship);
		databaseManager.removeFriendship(friendship);
	}
}
