package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Friendship;

public class DefaultFriendshipRepository implements FriendshipRepository {

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		return new DatabaseOperation<List<Friendship>>().
			setQueryString("SELECT f FROM Friendship f WHERE f.friend1.userId = :userId or f.friend2.userId = :userId").
			setParameter("userId", userId).
			query();
	}

	@Override
	public void saveFriendship(Friendship friendship) {
		new DatabaseOperation<Friendship>().persist(friendship);
	}
	
	@Override
	public void removeFriendship(Friendship friendship) {
		new DatabaseOperation<Friendship>().remove(friendship);
	}
}
