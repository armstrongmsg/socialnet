package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;

public interface FollowRepository {
	List<Follow> getFollowsByUserId(String userId);
	void saveFollow(Follow follow);
	void removeFollow(Follow follow);
}
