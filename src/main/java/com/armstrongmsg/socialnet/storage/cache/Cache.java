package com.armstrongmsg.socialnet.storage.cache;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.FollowAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FollowNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipNotFoundException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.FriendshipRequestNotFound;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

public interface Cache {
	void shutdown() throws InternalErrorException;
	
	void putUser(User user) throws UserAlreadyExistsException, InternalErrorException;
	User getUserById(String id) throws UserNotFoundException, InternalErrorException;
	User getUserByUsername(String username) throws UserNotFoundException, InternalErrorException;
	void updateUser(User user) throws UserNotFoundException, InternalErrorException;
	void removeUserById(String userId) throws UserNotFoundException, InternalErrorException;
	
	void putFriendship(Friendship friendship) throws FriendshipAlreadyExistsException, InternalErrorException;
	void putFriendships(List<Friendship> friendships) throws InternalErrorException;
	List<Friendship> getFriendshipsByUserId(String userId) throws InternalErrorException;
	List<Friendship> getFriendshipsByUsername(String username) throws InternalErrorException;
	void removeFriendship(Friendship friendship) throws FriendshipNotFoundException, InternalErrorException;
	
	void putFollow(Follow follow) throws FollowAlreadyExistsException, InternalErrorException;
	void putFollows(List<Follow> follows) throws InternalErrorException;
	List<Follow> getFollowsByUserId(String userId) throws InternalErrorException;
	List<Follow> getFollowsByUsername(String username) throws InternalErrorException;
	void removeFollow(Follow follow) throws FollowNotFoundException, InternalErrorException;
	
	void putFriendshipRequest(FriendshipRequest friendshipRequest) throws FriendshipRequestAlreadyExistsException, InternalErrorException;
	void putFriendshipRequests(List<FriendshipRequest> requests);
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId) throws InternalErrorException;
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) throws InternalErrorException;
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) throws FriendshipRequestNotFound, InternalErrorException;
	void removeFriendshipRequestById(FriendshipRequest friendshipRequest) throws FriendshipRequestNotFound, InternalErrorException;
}
