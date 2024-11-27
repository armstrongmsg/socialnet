package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

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
	public User getUserById(String id) throws UserNotFoundException {
		for (User user : this.users) {
			if (user.getUserId().equals(id)) {
				return user;
			}
		}

		// TODO add message
		throw new UserNotFoundException();
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		for (User user : this.users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		// TODO add message
		throw new UserNotFoundException();
	}

	@Override
	public void putUser(User user) throws UserAlreadyExistsException {
		if (!this.users.contains(user)) {
			this.users.add(user);
		} else {
			// TODO add message
			throw new UserAlreadyExistsException();
		}
	}

	@Override
	public void updateUser(User user) throws UserNotFoundException {
		if (!this.users.contains(user)) {
			// TODO add message
			throw new UserNotFoundException();
		} else {
			int index = this.users.indexOf(user);
			this.users.remove(index);
			this.users.add(index, user);
		}
	}

	@Override
	public void removeUserById(String userId) throws UserNotFoundException {
		User userToRemove = getUserById(userId);
		this.users.remove(userToRemove);
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
	public void putFriendship(Friendship friendship) throws FriendshipAlreadyExistsException {
		if (!this.friendships.contains(friendship)) {
			this.friendships.add(friendship);
		} else {
			// TODO add message
			throw new FriendshipAlreadyExistsException();
		}
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
		for (Friendship friendship : friendships) {
			try {
				putFriendship(friendship);
			} catch (FriendshipAlreadyExistsException e) {
				// TODO add message
			}
		}
	}

	@Override
	public void removeFriendship(Friendship friendship) throws FriendshipNotFoundException {
		if (!this.friendships.remove(friendship)) {
			// TODO add message
			throw new FriendshipNotFoundException();
		}
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
	public void putFollow(Follow follow) throws FollowAlreadyExistsException {
		if (!this.follows.contains(follow)) {
			this.follows.add(follow);
		} else {
			// TODO add message
			throw new FollowAlreadyExistsException();
		}
	}

	@Override
	public void putFollows(List<Follow> follows) {
		for (Follow follow : follows) {
			try {
				this.putFollow(follow);
			} catch (FollowAlreadyExistsException e) {
				// TODO add message
			}
		}
	}

	@Override
	public void removeFollow(Follow follow) throws FollowNotFoundException {
		if (!this.follows.remove(follow)) {
			// TODO add message
			throw new FollowNotFoundException();
		}
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) throws FriendshipRequestAlreadyExistsException {
		if (!this.friendshipRequests.contains(friendshipRequest)) {
			this.friendshipRequests.add(friendshipRequest);
		} else {
			// TODO add message
			throw new FriendshipRequestAlreadyExistsException();
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
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) throws FriendshipRequestNotFound {
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequested().getUserId().equals(userId) && 
					request.getRequester().getUsername().equals(username)) {
				return request;
			}
		}
		
		// TODO add message
		throw new FriendshipRequestNotFound();
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) throws FriendshipRequestNotFound {
		FriendshipRequest request = getReceivedFriendshipRequestById(friendshipRequest.getRequested().getUserId(), 
				friendshipRequest.getRequester().getUsername());
		this.friendshipRequests.remove(request);
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
		for (FriendshipRequest request : requests) {
			try {
				this.putFriendshipRequest(request);
			} catch (FriendshipRequestAlreadyExistsException e) {
				// TODO add message
			}
		}
	}

	@Override
	public void shutdown() {
		// does nothing
	}
}
