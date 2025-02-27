package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
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
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class LruCache implements Cache {
	private int totalCapacity;
	private List<User> users;
	private List<Friendship> friendships;
	private List<Follow> follows;
	private List<FriendshipRequest> friendshipRequests;
	
	public LruCache() throws FatalErrorException {
		String cacheMaxCapacityProperty = PropertiesUtil.getInstance().getProperty(
				ConfigurationProperties.CACHE_MAX_CAPACITY);
				
		if (cacheMaxCapacityProperty == null || cacheMaxCapacityProperty.isEmpty()) {
			throw new FatalErrorException(
					String.format(Messages.Exception.COULD_NOT_LOAD_CACHE_CONFIGURATION_PROPERTY, 
					ConfigurationProperties.CACHE_MAX_CAPACITY));
		}
		
		this.totalCapacity = Integer.valueOf(cacheMaxCapacityProperty);
		this.users = new ArrayList<User>();
		this.friendships = new ArrayList<Friendship>();
		this.follows = new ArrayList<Follow>();
		this.friendshipRequests = new ArrayList<FriendshipRequest>();
	}
	
	public LruCache(int totalCapacity) {
		this.totalCapacity = totalCapacity;
		this.users = new ArrayList<User>();
		this.friendships = new ArrayList<Friendship>();
		this.follows = new ArrayList<Follow>();
		this.friendshipRequests = new ArrayList<FriendshipRequest>();
	}
	
	@Override
	public User getUserById(String id) throws UserNotFoundException {
		User foundUser = null;
		
		for (User user : this.users) {
			if (user.getUserId().equals(id)) {
				foundUser = user;
				break;
			}
		}

		if (foundUser != null) {
			this.users.remove(foundUser);
			this.users.add(0, foundUser);
		} else {
			throw new UserNotFoundException();
		}
		
		return foundUser;
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		User foundUser = null;
		
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				foundUser = user;
				break;
			}
		}

		if (foundUser != null) {
			this.users.remove(foundUser);
			this.users.add(0, foundUser);
		} else {
			throw new UserNotFoundException();
		}
		
		return foundUser;
	}

	@Override
	public void putUser(User user) throws UserAlreadyExistsException {
		if (!this.users.contains(user)) {
			if (this.users.size() >= totalCapacity) {
				this.users.remove(this.users.size() - 1);
			}
			
			this.users.add(user);
		} else {
			throw new UserAlreadyExistsException();
		}
	}

	@Override
	public void updateUser(User user) throws UserNotFoundException, InternalErrorException {
		if (!this.users.contains(user)) {
			throw new UserNotFoundException();
		} else {
			int index = this.users.indexOf(user);
			this.users.remove(index);
			this.users.add(index, user);
		}
	}
	
	@Override
	public void removeUserById(String userId) throws UserNotFoundException {
		User user = getUserById(userId);
		this.users.remove(user);
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		List<Friendship> friendships = new ArrayList<Friendship>();
		
		for (Friendship friendship : this.friendships) {
			if (friendship.getFriend1().getUserId().equals(userId) || 
					friendship.getFriend2().getUserId().equals(userId)) {
				friendships.add(friendship);
			}
		}

		return friendships;
	}

	@Override
	public List<Friendship> getFriendshipsByUsername(String username) {
		List<Friendship> friendships = new ArrayList<Friendship>();
		
		for (Friendship friendship : this.friendships) {
			if (friendship.getFriend1().getUsername().equals(username) || 
					friendship.getFriend2().getUsername().equals(username)) {
				friendships.add(friendship);
			}
		}

		return friendships;
	}

	@Override
	public void putFriendship(Friendship friendship) throws FriendshipAlreadyExistsException {
		if (!this.friendships.contains(friendship)) {
			if (this.friendships.size() >= this.totalCapacity) {
				friendships.remove(0);
			}
			
			friendships.add(friendship);
		} else {
			throw new FriendshipAlreadyExistsException();
		}
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
		for (Friendship friendship : friendships) {
			try {
				putFriendship(friendship);
			} catch (FriendshipAlreadyExistsException e) {
			}
		}
	}

	@Override
	public void removeFriendship(Friendship friendship) throws FriendshipNotFoundException {
		if (!this.friendships.remove(friendship)) {
			throw new FriendshipNotFoundException();
		}
	}
	
	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		List<Follow> follows = new ArrayList<Follow>();
		
		for (Follow follow : this.follows) {
			if (follow.getFollowed().getUserId().equals(userId) || 
					follow.getFollower().getUserId().equals(userId)) {
				follows.add(follow);
			}
		}

		return follows;
	}

	@Override
	public List<Follow> getFollowsByUsername(String username) {
		List<Follow> follows = new ArrayList<Follow>();
		
		for (Follow follow : this.follows) {
			if (follow.getFollowed().getUsername().equals(username) || 
					follow.getFollower().getUsername().equals(username)) {
				follows.add(follow);
			}
		}

		return follows;
	}

	@Override
	public void putFollow(Follow follow) throws FollowAlreadyExistsException {
		if (!this.follows.contains(follow)) {
			if (this.follows.size() >= this.totalCapacity) {
				follows.remove(0);
			}
			
			follows.add(follow);		
		} else {
			throw new FollowAlreadyExistsException();
		}
	}

	@Override
	public void putFollows(List<Follow> follows) {
		for (Follow follow : follows) {
			try {
				putFollow(follow);
			} catch (FollowAlreadyExistsException e) {
			}
		}
	}

	@Override
	public void removeFollow(Follow follow) throws FollowNotFoundException {
		if (!this.follows.remove(follow)) {
			throw new FollowNotFoundException();
		}
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) throws FriendshipRequestAlreadyExistsException {
		if (!this.friendshipRequests.contains(friendshipRequest)) {
			if (this.friendshipRequests.size() >= this.totalCapacity) {
				friendshipRequests.remove(0);
			}
			
			friendshipRequests.add(friendshipRequest);		
		} else {
			throw new FriendshipRequestAlreadyExistsException();
		}
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
		for (FriendshipRequest request : requests) {
			try {
				putFriendshipRequest(request);
			} catch (FriendshipRequestAlreadyExistsException e) {
			}
		}
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		List<FriendshipRequest> friendshipRequests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest friendshipRequest : this.friendshipRequests) {
			if (friendshipRequest.getRequester().getUserId().equals(userId)) {
				friendshipRequests.add(friendshipRequest);
			}
		}

		return friendshipRequests;
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		List<FriendshipRequest> friendshipRequests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest friendshipRequest : this.friendshipRequests) {
			if (friendshipRequest.getRequested().getUserId().equals(userId)) {
				friendshipRequests.add(friendshipRequest);
			}
		}

		return friendshipRequests;
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) throws FriendshipRequestNotFound {
		for (FriendshipRequest friendshipRequest : this.friendshipRequests) {
			if (friendshipRequest.getRequested().getUserId().equals(userId) && 
					friendshipRequest.getRequester().getUsername().equals(username)) {
				return friendshipRequest;
			}
		}

		throw new FriendshipRequestNotFound();
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) throws FriendshipRequestNotFound {
		if (!this.friendshipRequests.remove(friendshipRequest)) {
			throw new FriendshipRequestNotFound();
		}
	}

	@Override
	public void shutdown() {
		// does nothing
	}
}
