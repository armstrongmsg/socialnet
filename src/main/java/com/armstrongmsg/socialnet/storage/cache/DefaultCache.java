package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

// TODO test
public class DefaultCache implements Cache {
	private List<User> users;
	private List<Friendship> friendships;
	private List<Follow> follows;
	private List<FriendshipRequest> friendshipRequests;

	public DefaultCache() {
		this.users = new ArrayList<User>();
		this.friendships = new ArrayList<Friendship>();
		this.follows = new ArrayList<Follow>();
		this.friendshipRequests = new ArrayList<FriendshipRequest>();
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
		if (!this.users.contains(user)) {
			this.users.add(user);
		}
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
		if (!this.friendships.contains(friendship)) {
			this.friendships.add(friendship);
		}
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
		for (Friendship friendship : friendships) {
			putFriendship(friendship);
		}
	}

	@Override
	public void removeFriendship(Friendship friendship) {
		this.friendships.remove(friendship);
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
		if (!this.follows.contains(follow)) {
			this.follows.add(follow);
		}
	}

	@Override
	public void putFollows(List<Follow> follows) {
		for (Follow follow : follows) {
			this.putFollow(follow);
		}
	}

	@Override
	public void removeFollow(Follow follow) {
		this.follows.remove(follow);
	}
	
	public List<User> getUsers() {
		return users;
	}

	@Override
	public void removeUserById(String userId) {
		User userToRemove = getUserById(userId);
		this.users.remove(userToRemove);
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) {
		if (!this.friendshipRequests.contains(friendshipRequest)) {
			this.friendshipRequests.add(friendshipRequest);
		}
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequester().getUserId().equals(userId)) {
				requests.add(request);
			}
		}
		
		return requests;
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequested().getUserId().equals(userId)) {
				requests.add(request);
			}
		}
		
		return requests;
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequested().getUserId().equals(userId) && 
					request.getRequester().getUsername().equals(username)) {
				return request;
			}
		}
		
		return null;
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) {
		FriendshipRequest request = getReceivedFriendshipRequestById(friendshipRequest.getRequested().getUserId(), 
				friendshipRequest.getRequester().getUsername());
		this.friendshipRequests.remove(request);
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
		for (FriendshipRequest request : requests) {
			this.putFriendshipRequest(request);
		}
	}

	@Override
	public void shutdown() {
		// does nothing
	}
}
