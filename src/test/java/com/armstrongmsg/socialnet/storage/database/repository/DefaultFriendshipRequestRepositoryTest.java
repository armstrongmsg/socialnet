package com.armstrongmsg.socialnet.storage.database.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;
import com.armstrongmsg.socialnet.util.PersistenceTest;

public class DefaultFriendshipRequestRepositoryTest extends PersistenceTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_ID_2 = "userId2";
	private static final String USER_NAME_1 = "username1";
	private static final String USER_NAME_2 = "username2";
	private static final String USER_DESCRIPTION_1 = "userDescription1";
	private static final String USER_DESCRIPTION_2 = "userDescription2";
	private static final String PASSWORD_1 = "password1";
	private static final String PASSWORD_2 = "password2";
	private UserRepository userRepository;
	private FriendshipRequestRepository friendshipRequestRepository;
	private FriendshipRequest friendshipRequest1;
	private User friend1;
	private User friend2;
	private Profile profile1;
	private Profile profile2;
	private DatabaseConnectionManager connectionManager;
	
	@Before
	@Override
	public void setUp() throws FatalErrorException {
		super.setUp();
		
		connectionManager = super.getConnectionManager();
		
		profile1 = new Profile(USER_DESCRIPTION_1, new ArrayList<Post>());
		friend1 = new User(USER_ID_1, USER_NAME_1, PASSWORD_1, profile1);		
		profile2 = new Profile(USER_DESCRIPTION_2, new ArrayList<Post>());
		friend2 = new User(USER_ID_2, USER_NAME_2, PASSWORD_2, profile2);
		
		userRepository = new DefaultUserRepository(connectionManager);
		userRepository.saveUser(friend1);
		userRepository.saveUser(friend2);
		
		connectionManager.close();
	}
	
	@Test
	public void testFriendshipRequestsPersistence() {
		connectionManager = super.getConnectionManager();
		
		friendshipRequest1 = new FriendshipRequest(friend1, friend2);
		friendshipRequestRepository = new DefaultFriendshipRequestRepository(connectionManager);
		
		List<FriendshipRequest> requestsReceivedByUser1 = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsReceivedByUser2 = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_2);
		List<FriendshipRequest> requestsSentByUser1 = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsSentByUser2 = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_2);
		
		assertTrue(requestsReceivedByUser1.isEmpty());
		assertTrue(requestsReceivedByUser2.isEmpty());
		assertTrue(requestsSentByUser1.isEmpty());
		assertTrue(requestsSentByUser2.isEmpty());
		assertNull(friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_1, USER_NAME_2));
		assertNull(friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1));
		
		friendshipRequestRepository.add(friendshipRequest1);
		
		// Shutdown connections
		connectionManager.close();
				
		// Restart db connection
		connectionManager = super.getConnectionManager();
		friendshipRequestRepository = new DefaultFriendshipRequestRepository(connectionManager);
		
		List<FriendshipRequest> requestsReceivedByUser1AfterSave = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsReceivedByUser2AfterSave = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_2);
		List<FriendshipRequest> requestsSentByUser1AfterSave = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsSentByUser2AfterSave = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_2);
		
		assertTrue(requestsReceivedByUser1AfterSave.isEmpty());
		
		assertEquals(1, requestsReceivedByUser2AfterSave.size());
		assertEquals(friend1, requestsReceivedByUser2AfterSave.get(0).getRequester());
		assertEquals(friend2, requestsReceivedByUser2AfterSave.get(0).getRequested());
		
		assertEquals(1, requestsSentByUser1AfterSave.size());
		assertEquals(friend1, requestsSentByUser1AfterSave.get(0).getRequester());
		assertEquals(friend2, requestsSentByUser1AfterSave.get(0).getRequested());
		
		assertTrue(requestsSentByUser2AfterSave.isEmpty());
		
		assertNull(friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_1, USER_NAME_2));
		FriendshipRequest retrievedRequest = friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1); 
		assertEquals(friend1, retrievedRequest.getRequester());
		assertEquals(friend2, retrievedRequest.getRequested());
		
		friendshipRequestRepository.removeFriendshipRequest(friendshipRequest1);
		
		// Shutdown connections
		connectionManager.close();
						
		// Restart db connection
		connectionManager = super.getConnectionManager();
		friendshipRequestRepository = new DefaultFriendshipRequestRepository(connectionManager);
		
		List<FriendshipRequest> requestsReceivedByUser1AfterRemoval = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsReceivedByUser2AfterRemoval = friendshipRequestRepository.getReceivedFriendshipRequestsById(USER_ID_2);
		List<FriendshipRequest> requestsSentByUser1AfterRemoval = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_1);
		List<FriendshipRequest> requestsSentByUser2AfterRemoval = friendshipRequestRepository.getSentFriendshipRequestsById(USER_ID_2);
		assertTrue(requestsReceivedByUser1AfterRemoval.isEmpty());
		assertTrue(requestsReceivedByUser2AfterRemoval.isEmpty());
		assertTrue(requestsSentByUser1AfterRemoval.isEmpty());
		assertTrue(requestsSentByUser2AfterRemoval.isEmpty());
		assertNull(friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_1, USER_NAME_2));
		assertNull(friendshipRequestRepository.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1));
		
		connectionManager.close();
	}
}
