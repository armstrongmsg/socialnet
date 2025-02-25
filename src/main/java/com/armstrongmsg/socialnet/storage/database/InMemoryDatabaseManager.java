package com.armstrongmsg.socialnet.storage.database;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

public class InMemoryDatabaseManager implements DatabaseManager {
	private List<User> users;
	private List<Friendship> friendships;
	private List<Follow> follows;
	private List<FriendshipRequest> friendshipRequests;

	public InMemoryDatabaseManager() {
		this.users = new ArrayList<User>();
		this.friendships = new ArrayList<Friendship>();
		this.follows = new ArrayList<Follow>();
		this.friendshipRequests = new ArrayList<FriendshipRequest>();
	}

	@Override
	public User getUserById(String id) throws UserNotFoundException {
		for (User user : this.users) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}

		throw new UserNotFoundException(id);
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}

		throw new UserNotFoundException(username);
	}

	@Override
	public User saveUser(User user) {
		this.users.add(user);
		return user;
	}
	
	@Override
	public User updateUser(User user) throws UserNotFoundException {
		getUserById(user.getUserId());
		users.remove(user);
		users.add(user);
		return user;
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
	public Friendship saveFriendship(Friendship friendship) {
		this.friendships.add(friendship);
		return friendship;
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
	public Follow saveFollow(Follow follow) {
		this.follows.add(follow);
		return follow;
	}

	@Override
	public void removeFollow(Follow follow) {
		this.follows.remove(follow);
	}

	@Override
	public List<User> getAllUsers() {
		return this.users;
	}

	@Override
	public void removeUserById(String userId) throws UserNotFoundException {
		User userToRemove = getUserById(userId);
		this.users.remove(userToRemove);
	}

	@Override
	public FriendshipRequest saveFriendshipRequest(FriendshipRequest friendshipRequest) {
		this.friendshipRequests.add(friendshipRequest);
		return friendshipRequest;
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
			if (request.getRequested().getUserId().equals(userId)
					&& request.getRequester().getUsername().equals(username)) {
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
	public void shutdown() {
		// does nothing
	}
}
