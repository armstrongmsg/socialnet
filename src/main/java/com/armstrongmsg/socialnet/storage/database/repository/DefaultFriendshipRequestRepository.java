package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;

public class DefaultFriendshipRequestRepository implements FriendshipRequestRepository {
	private DatabaseConnectionManager connectionManager;
	
	public DefaultFriendshipRequestRepository(DatabaseConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public void add(FriendshipRequest friendshipRequest) {
		new DatabaseOperation<FriendshipRequest>(connectionManager).persist(friendshipRequest);
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return new DatabaseOperation<List<FriendshipRequest>>(connectionManager).
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requester.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		return new DatabaseOperation<List<FriendshipRequest>>(connectionManager).
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		List<FriendshipRequest> result = new DatabaseOperation<List<FriendshipRequest>>(connectionManager).
				setQueryString("SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId and f.requester.username = :username").
				setParameter("userId", userId).
				setParameter("username", username).
				query();
		
		if (result.size() >= 1) {
			return result.get(0);
		}
		
		return null;
	}

	@Override
	public void removeFriendshipRequest(FriendshipRequest friendshipRequest) {
		new DatabaseOperation<FriendshipRequest>(connectionManager).remove(friendshipRequest);
	}
}
