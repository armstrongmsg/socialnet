package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.FriendshipRequest;

public interface FriendshipRequestRepository {
	void add(FriendshipRequest friendshipRequest);
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId);
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId);
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username);
	void removeFriendshipRequest(FriendshipRequest friendshipRequest);
}
