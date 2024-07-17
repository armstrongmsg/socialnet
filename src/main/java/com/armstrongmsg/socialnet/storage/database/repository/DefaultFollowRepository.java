package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;

public class DefaultFollowRepository implements FollowRepository {
	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		return new DatabaseOperation<List<Follow>>().
				setQueryString("SELECT f FROM Follow f WHERE f.follower.userId = :userId or f.followed.userId = :userId").
				setParameter("userId", userId).
				query();
	}

	@Override
	public void saveFollow(Follow follow) {
		new DatabaseOperation<Follow>().persist(follow);
	}

	@Override
	public void removeFollow(Follow follow) {
		new DatabaseOperation<Follow>().remove(follow);
	}
}
