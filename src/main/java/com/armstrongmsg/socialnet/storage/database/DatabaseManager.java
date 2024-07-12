package com.armstrongmsg.socialnet.storage.database;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public interface DatabaseManager {
	void saveUser(User user);
	User getUserById(String userId) throws UserNotFoundException;
	User getUserByUsername(String username) throws UserNotFoundException;
	void removeUserById(String userId) throws UserNotFoundException;

	Group getGroupById(String groupId);
	Group getGroupByName(String groupName);
	void saveGroup(Group group);

	List<Friendship> getFriendshipsByUserId(String userId);
	void saveFriendship(Friendship friendship);
	
	List<Follow> getFollowsByUserId(String userId);
	void saveFollow(Follow follow);
	List<User> getAllUsers();
	void removeFollow(Follow follow);
	
	void saveFriendshipRequest(FriendshipRequest friendshipRequest);
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId);
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId);
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username);
	void removeFriendshipRequestById(String userId, String username);
	void removeFriendship(Friendship friendship);
	void updateUser(User user);
}
