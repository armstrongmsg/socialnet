package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.Friendship;

public interface FriendshipRepository {
	List<Friendship> getFriendshipsByUserId(String userId);
	void saveFriendship(Friendship friendship);
}
