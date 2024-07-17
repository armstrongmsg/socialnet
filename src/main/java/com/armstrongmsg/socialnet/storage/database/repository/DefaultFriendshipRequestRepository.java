package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.FriendshipRequest;

public class DefaultFriendshipRequestRepository implements FriendshipRequestRepository {

	@Override
	public void add(FriendshipRequest friendshipRequest) {
		new DatabaseOperation<FriendshipRequest>().persist(friendshipRequest);
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return new DatabaseOperation<List<FriendshipRequest>>().
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requester.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		return new DatabaseOperation<List<FriendshipRequest>>().
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		return new DatabaseOperation<List<FriendshipRequest>>().
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId and f.requester.username = :username").
				setParameter("userId", userId).
				setParameter("username", username).
				query().get(0);
	}

	@Override
	public void removeFriendshipRequest(FriendshipRequest friendshipRequest) {
		new DatabaseOperation<FriendshipRequest>().remove(friendshipRequest);
	}
}
