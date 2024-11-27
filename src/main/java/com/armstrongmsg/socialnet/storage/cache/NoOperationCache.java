package com.armstrongmsg.socialnet.storage.cache;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

public class NoOperationCache implements Cache {

	@Override
	public User getUserById(String id) throws UserNotFoundException {
		throw new UserNotFoundException();
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		throw new UserNotFoundException();
	}

	@Override
	public void putUser(User user) {
	}
	
	@Override
	public void updateUser(User user) throws UserNotFoundException, InternalErrorException {
	}

	@Override
	public void removeUserById(String userId) {
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		return new ArrayList<Friendship>();
	}

	@Override
	public List<Friendship> getFriendshipsByUsername(String username) {
		return new ArrayList<Friendship>();
	}

	@Override
	public void putFriendship(Friendship friendship) {
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		return new ArrayList<Follow>();
	}

	@Override
	public List<Follow> getFollowsByUsername(String username) {
		return new ArrayList<Follow>();
	}

	@Override
	public void putFollow(Follow follow) {
	}

	@Override
	public void putFollows(List<Follow> follows) {
	}

	@Override
	public void removeFollow(Follow follow) {
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) {
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return new ArrayList<FriendshipRequest>();
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		return new ArrayList<FriendshipRequest>();
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) throws FriendshipRequestNotFound {
		throw new FriendshipRequestNotFound();
	}

	@Override
	public void removeFriendship(Friendship friendship) {
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) {
	}

	@Override
	public void shutdown() {
		// does nothing
	}
}
