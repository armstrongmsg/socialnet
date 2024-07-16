package com.armstrongmsg.socialnet.storage.database;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultFollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultFriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultUserRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.UserRepository;

public class DefaultDatabaseManager implements DatabaseManager {
	private List<FriendshipRequest> friendshipRequests;
	private UserRepository userRepository;
	private FriendshipRepository friendshipRepository;
	private FollowRepository followRepository;
	
	public DefaultDatabaseManager() {
		this.userRepository = new DefaultUserRepository();
		this.friendshipRepository = new DefaultFriendshipRepository();
		this.followRepository = new DefaultFollowRepository();
		this.friendshipRequests = new ArrayList<FriendshipRequest>();
	}

	@Override
	public User getUserById(String id) throws UserNotFoundException {
		User user = this.userRepository.getUserById(id);
		
		if (user == null) {
			throw new UserNotFoundException(id);	
		}
		
		return user;
	}

	@Override
	public User getUserByUsername(String username) throws UserNotFoundException {
		User user = this.userRepository.getUserByUsername(username);
		
		if (user == null) {
			throw new UserNotFoundException(username);	
		}
		
		return user;
	}

	@Override
	public void saveUser(User user) {
		this.userRepository.saveUser(user);
	}
	
	@Override
	public void updateUser(User user) {
		this.userRepository.updateUser(user);
	}
	
	@Override
	public List<User> getAllUsers() {
		return this.userRepository.getAllUsers();
	}

	@Override
	public void removeUserById(String userId) throws UserNotFoundException {
		this.userRepository.removeUserById(userId);
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
	public void saveGroup(Group group) {
	}

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		return this.friendshipRepository.getFriendshipsByUserId(userId);
	}

	@Override
	public void saveFriendship(Friendship friendship) {
		this.friendshipRepository.saveFriendship(friendship);
	}
	
	@Override
	public void removeFriendship(Friendship friendship) {
		this.friendshipRepository.removeFriendship(friendship);
	}

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		return this.followRepository.getFollowsByUserId(userId);
	}

	@Override
	public void saveFollow(Follow follow) {
		this.followRepository.saveFollow(follow);
	}

	@Override
	public void removeFollow(Follow follow) {
		this.followRepository.removeFollow(follow);
	}

	@Override
	public void saveFriendshipRequest(FriendshipRequest friendshipRequest) {
		this.friendshipRequests.add(friendshipRequest);
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequester().getUserId().equals(userId)) {
				requests.add(request);
			}
		}
		
		return requests;
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
		
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequested().getUserId().equals(userId)) {
				requests.add(request);
			}
		}
		
		return requests;
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		for (FriendshipRequest request : this.friendshipRequests) {
			if (request.getRequested().getUserId().equals(userId) && 
					request.getRequester().getUsername().equals(username)) {
				return request;
			}
		}
		
		return null;
	}

	@Override
	public void removeFriendshipRequestById(String userId, String username) {
		FriendshipRequest request = getReceivedFriendshipRequestById(userId, username);
		this.friendshipRequests.remove(request);
	}
}
