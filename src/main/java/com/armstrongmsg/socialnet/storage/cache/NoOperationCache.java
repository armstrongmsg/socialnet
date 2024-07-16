package com.armstrongmsg.socialnet.storage.cache;

import java.util.List;

import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;

public class NoOperationCache implements Cache {

	@Override
	public User getUserById(String id) {
		return null;
	}

	@Override
	public User getUserByUsername(String username) {
		return null;
	}

	@Override
	public void putUser(User user) {
	}

	@Override
	public void removeUserById(String userId) {
	}

	@Override
	public Group getGroupById(String id) {
		return null;
	}

	@Override
	public Group getGroupByName(String name) {
		return null;
	}

	@Override
	public void putGroup(Group group) {
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		return null;
	}

	@Override
	public List<Friendship> getFriendshipsByUsername(String username) {
		return null;
	}

	@Override
	public void putFriendship(Friendship friendship) {
	}

	@Override
	public void putFriendships(List<Friendship> friendships) {
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		return null;
	}

	@Override
	public List<Follow> getFollowsByUsername(String username) {
		return null;
	}

	@Override
	public void putFollow(Follow follow) {
	}

	@Override
	public void putFollows(List<Follow> follows) {
	}

	@Override
	public void removeFollow(Follow follow) {
	}

	@Override
	public void putFriendshipRequest(FriendshipRequest friendshipRequest) {
	}

	@Override
	public void putFriendshipRequests(List<FriendshipRequest> requests) {
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return null;
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		return null;
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		return null;
	}

	@Override
	public void removeFriendshipRequestById(String userId, String username) {
	}

	@Override
	public void removeFriendship(Friendship friendship) {
	}
}
