package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO test
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
			// TODO add message
			throw new FatalErrorException();
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
	public User getUserById(String id) {
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
		}
		
		return foundUser;
	}

	@Override
	public User getUserByUsername(String username) {
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
		}
		
		return foundUser;
	}

	@Override
	public void putUser(User user) {
		if (!this.users.contains(user)) {
			if (this.users.size() >= totalCapacity) {
				this.users.remove(0);
			}
			
			this.users.add(user);
		}
	}

	@Override
	public void removeUserById(String userId) {
		User user = getUserById(userId);
		this.users.remove(user);
	}

	@Override
	public Group getGroupById(String id) {
		return null;
	}

	@Override
	public Group getGroupByName(String name) {
		return null;
	}

	@Override
	public void putGroup(Group group) {

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
	public void putFriendship(Friendship friendship) {
		if (!this.friendships.contains(friendship)) {
			if (this.friendships.size() >= this.totalCapacity) {
				friendships.remove(0);
			}
			
			friendships.add(friendship);
		}
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
		for (Friendship friendship : friendships) {
			putFriendship(friendship);
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
	public void putFollow(Follow follow) {
		if (!this.follows.contains(follow)) {
			if (this.follows.size() >= this.totalCapacity) {
				follows.remove(0);
			}
			
			follows.add(follow);		
		}
	}

	@Override
	public void putFollows(List<Follow> follows) {
		for (Follow follow : follows) {
			putFollow(follow);
		}
	}

	@Override
	public void removeFollow(Follow follow) {
		this.follows.remove(follow);
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) {
		if (!this.friendshipRequests.contains(friendshipRequest)) {
			if (this.friendshipRequests.size() >= this.totalCapacity) {
				friendshipRequests.remove(0);
			}
			
			friendshipRequests.add(friendshipRequest);		
		}
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
		for (FriendshipRequest request : requests) {
			putFriendshipRequest(request);
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
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		for (FriendshipRequest friendshipRequest : this.friendshipRequests) {
			if (friendshipRequest.getRequested().getUserId().equals(userId) && 
					friendshipRequest.getRequester().getUsername().equals(username)) {
				return friendshipRequest;
			}
		}

		return null;
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) {
		this.friendshipRequests.remove(friendshipRequest);
	}

	@Override
	public void removeFriendship(Friendship friendship) {
		this.friendships.remove(friendship);
	}

	@Override
	public void shutdown() {
		// does nothing
	}
}
