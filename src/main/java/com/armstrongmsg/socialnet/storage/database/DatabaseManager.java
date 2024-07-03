package com.armstrongmsg.socialnet.storage.database;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public interface DatabaseManager {
	void saveUser(User user);
	User getUserById(String userId);
	User getUserByUsername(String username);
	void removeUserById(String userId);

	Group getGroupById(String groupId);
	Group getGroupByName(String groupName);
	void saveGroup(Group group);

	List<Friendship> getFriendshipsByUserId(String userId);
	List<Friendship> getFriendshipsByUsername(String username);
	void saveFriendship(Friendship friendship);
	
	List<Follow> getFollowsByUserId(String userId);
	List<Follow> getFollowsByUsername(String username);
	void saveFollow(Follow follow);
	List<User> getAllUsers();
	void removeFollow(Follow follow);
	
	void saveFriendshipRequest(FriendshipRequest friendshipRequest);
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId);
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId);
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username);
	void removeFriendshipRequestById(String userId, String username);
}
