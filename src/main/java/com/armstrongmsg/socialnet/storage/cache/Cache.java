package com.armstrongmsg.socialnet.storage.cache;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public interface Cache {
	User getUserById(String id);
	User getUserByUsername(String username);
	void putUser(User user);
	void removeUserById(String userId);

	Group getGroupById(String id);
	Group getGroupByName(String name);
	void putGroup(Group group);
	
	List<Friendship> getFriendshipsByUserId(String userId);
	List<Friendship> getFriendshipsByUsername(String username);
	void putFriendship(Friendship friendship);
	void putFriendships(List<Friendship> friendships);
	
	List<Follow> getFollowsByUserId(String userId);
	List<Follow> getFollowsByUsername(String username);
	void putFollow(Follow follow);
	void putFollows(List<Follow> follows);
	void removeFollow(Follow follow);
	
	void putFriendshipRequest(FriendshipRequest friendshipRequest);
	void putFriendshipRequests(List<FriendshipRequest> requests);
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId);
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId);
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username);
	void removeFriendshipRequestById(FriendshipRequest friendshipRequest);
	void removeFriendship(Friendship friendship);
	
	void shutdown();
}
