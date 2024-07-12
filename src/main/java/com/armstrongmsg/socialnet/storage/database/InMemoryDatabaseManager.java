package com.armstrongmsg.socialnet.storage.database;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
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
	public void saveUser(User user) {
		this.users.add(user);
	}
	
	@Override
	public void updateUser(User user) {
		
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
	public void saveGroup(Group group) {
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
	public void saveFriendship(Friendship friendship) {
		this.friendships.add(friendship);
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
	public void saveFollow(Follow follow) {
		this.follows.add(follow);
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
	public void saveFriendshipRequest(FriendshipRequest friendshipRequest) {
		this.friendshipRequests.add(friendshipRequest);
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
	public void removeFriendshipRequestById(String userId, String username) {
		FriendshipRequest request = getReceivedFriendshipRequestById(userId, username);
		this.friendshipRequests.remove(request);
	}
}
