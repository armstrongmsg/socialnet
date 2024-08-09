package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;

public class DefaultFriendshipRepository implements FriendshipRepository {
	private DatabaseConnectionManager connectionManager;
	
	public DefaultFriendshipRepository(DatabaseConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		return new DatabaseOperation<List<Friendship>>(connectionManager).
			setQueryString("SELECT f FROM Friendship f WHERE f.friend1.userId = :userId or f.friend2.userId = :userId").
			setParameter("userId", userId).
			query();
	}

	@Override
	public void saveFriendship(Friendship friendship) {
		new DatabaseOperation<Friendship>(connectionManager).persist(friendship);
	}
	
	@Override
	public void removeFriendship(Friendship friendship) {
		new DatabaseOperation<Friendship>(connectionManager).remove(friendship);
	}
}
