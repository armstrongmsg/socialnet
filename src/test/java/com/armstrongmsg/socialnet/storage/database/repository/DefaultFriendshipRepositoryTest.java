package com.armstrongmsg.socialnet.storage.database.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;
import com.armstrongmsg.socialnet.util.PersistenceTest;

public class DefaultFriendshipRepositoryTest extends PersistenceTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_ID_2 = "userId2";
	private static final String USER_NAME_1 = "username1";
	private static final String USER_NAME_2 = "username2";
	private static final String USER_DESCRIPTION_1 = "userDescription1";
	private static final String USER_DESCRIPTION_2 = "userDescription2";
	private static final String PASSWORD_1 = "password1";
	private static final String PASSWORD_2 = "password2";
	private UserRepository userRepository;
	private FriendshipRepository friendshipRepository;
	private Friendship friendship1;
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
	public void testFriendshipsPersistence() {
		connectionManager = super.getConnectionManager();
		
		friendship1 = new Friendship(friend1, friend2);
		friendshipRepository = new DefaultFriendshipRepository(connectionManager);
		
		List<Friendship> friendshipsByUser1 = friendshipRepository.getFriendshipsByUserId(USER_ID_1);
		List<Friendship> friendshipsByUser2 = friendshipRepository.getFriendshipsByUserId(USER_ID_2);
		
		assertTrue(friendshipsByUser1.isEmpty());
		assertTrue(friendshipsByUser2.isEmpty());
		
		friendshipRepository.saveFriendship(friendship1);
		
		// Shutdown connections
		connectionManager.close();
				
		// Restart db connection
		connectionManager = super.getConnectionManager();
		friendshipRepository = new DefaultFriendshipRepository(connectionManager);
		
		List<Friendship> friendshipsAfterSaveByUser1 = friendshipRepository.getFriendshipsByUserId(USER_ID_1);
		List<Friendship> friendshipsAfterSaveByUser2 = friendshipRepository.getFriendshipsByUserId(USER_ID_2);
		
		assertEquals(1, friendshipsAfterSaveByUser1.size());
		assertEquals(friend1, friendshipsAfterSaveByUser1.get(0).getFriend1());
		assertEquals(friend2, friendshipsAfterSaveByUser1.get(0).getFriend2());
		
		assertEquals(1, friendshipsAfterSaveByUser2.size());
		assertEquals(friend1, friendshipsAfterSaveByUser2.get(0).getFriend1());
		assertEquals(friend2, friendshipsAfterSaveByUser2.get(0).getFriend2());
		
		friendshipRepository.removeFriendship(friendship1);
		
		// Shutdown connections
		connectionManager.close();
						
		// Restart db connection
		connectionManager = super.getConnectionManager();
		friendshipRepository = new DefaultFriendshipRepository(connectionManager);
		
		List<Friendship> friendshipsAfterRemovalByUser1 = friendshipRepository.getFriendshipsByUserId(USER_ID_1);
		List<Friendship> friendshipsAfterRemovalByUser2 = friendshipRepository.getFriendshipsByUserId(USER_ID_2);
		assertTrue(friendshipsAfterRemovalByUser1.isEmpty());
		assertTrue(friendshipsAfterRemovalByUser2.isEmpty());
		
		connectionManager.close();
	}
}
