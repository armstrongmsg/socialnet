package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;

public class DefaultFollowRepository implements FollowRepository {
	private DatabaseConnectionManager connectionManager;
	
	public DefaultFollowRepository(DatabaseConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		return new DatabaseOperation<List<Follow>>(connectionManager).
				setQueryString("SELECT f FROM Follow f WHERE f.follower.userId = :userId or f.followed.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public void saveFollow(Follow follow) {
		new DatabaseOperation<Follow>(connectionManager).persist(follow);
	}

	@Override
	public void removeFollow(Follow follow) {
		new DatabaseOperation<Follow>(connectionManager).remove(follow);
	}
}
