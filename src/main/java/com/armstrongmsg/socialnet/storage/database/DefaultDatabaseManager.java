package com.armstrongmsg.socialnet.storage.database;

import java.util.List;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;
import com.armstrongmsg.socialnet.storage.database.connection.PoolBasedDatabaseConnectionManager;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultFollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultFriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultFriendshipRequestRepository;
import com.armstrongmsg.socialnet.storage.database.repository.DefaultUserRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRequestRepository;
import com.armstrongmsg.socialnet.storage.database.repository.UserRepository;

public class DefaultDatabaseManager implements DatabaseManager {
	private UserRepository userRepository;
	private FriendshipRepository friendshipRepository;
	private FollowRepository followRepository;
	private FriendshipRequestRepository friendshipRequestsRepository;
	
	private DatabaseConnectionManager connectionManager;
	
	public DefaultDatabaseManager(UserRepository userRepository, 
			FriendshipRepository friendshipRepository, FollowRepository followRepository,
			FriendshipRequestRepository friendshipRequestsRepository) {
		this.userRepository = userRepository;
		this.friendshipRepository = friendshipRepository;
		this.followRepository = followRepository;
		this.friendshipRequestsRepository = friendshipRequestsRepository;
	}
	
	public DefaultDatabaseManager() throws FatalErrorException {
		this.connectionManager = new PoolBasedDatabaseConnectionManager();
		this.userRepository = new DefaultUserRepository(this.connectionManager);
		this.friendshipRepository = new DefaultFriendshipRepository(this.connectionManager);
		this.followRepository = new DefaultFollowRepository(this.connectionManager);
		this.friendshipRequestsRepository = new DefaultFriendshipRequestRepository(this.connectionManager);
	}

	@Override
	public void shutdown() {
		this.connectionManager.close();
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
	public void updateUser(User user) throws UserNotFoundException {
		this.userRepository.updateUser(user);
	}
	
	@Override
	public List<User> getAllUsers() {
		List<User> users = this.userRepository.getAllUsers();
		return users;
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
		this.friendshipRequestsRepository.add(friendshipRequest);
	}

	@Override
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		return this.friendshipRequestsRepository.getSentFriendshipRequestsById(userId);
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {	
		return this.friendshipRequestsRepository.getReceivedFriendshipRequestsById(userId);
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {		
		return this.friendshipRequestsRepository.getReceivedFriendshipRequestById(userId, username);
	}

	@Override
	public void removeFriendshipRequestById(FriendshipRequest friendshipRequest) {
		this.friendshipRequestsRepository.removeFriendshipRequest(friendshipRequest);
	}
}
