package com.armstrongmsg.socialnet.storage.database;

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

public interface DatabaseManager {
	void shutdown() throws InternalErrorException;
	
	User saveUser(User user) throws UserAlreadyExistsException, InternalErrorException;
	User getUserById(String userId) throws UserNotFoundException, InternalErrorException;
	User getUserByUsername(String username) throws UserNotFoundException, InternalErrorException;
	User updateUser(User user) throws UserNotFoundException, InternalErrorException;
	List<User> getAllUsers() throws InternalErrorException;
	void removeUserById(String userId) throws UserNotFoundException, InternalErrorException;
	
	Friendship saveFriendship(Friendship friendship) 
			throws FriendshipAlreadyExistsException, InternalErrorException;
	List<Friendship> getFriendshipsByUserId(String userId) throws InternalErrorException;
	void removeFriendship(Friendship friendship) 
			throws FriendshipNotFoundException, InternalErrorException;
	
	Follow saveFollow(Follow follow) throws FollowAlreadyExistsException, InternalErrorException;
	List<Follow> getFollowsByUserId(String userId) throws InternalErrorException;
	void removeFollow(Follow follow) throws FollowNotFoundException, InternalErrorException;
	
	FriendshipRequest saveFriendshipRequest(FriendshipRequest friendshipRequest) 
			throws FriendshipRequestAlreadyExistsException, InternalErrorException;
	List<FriendshipRequest> getSentFriendshipRequestsById(String userId) throws InternalErrorException;
	List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) throws InternalErrorException;
	FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) 
			throws FriendshipRequestNotFound, InternalErrorException;
	void removeFriendshipRequestById(FriendshipRequest friendshipRequest) 
			throws FriendshipRequestNotFound, InternalErrorException;
}
