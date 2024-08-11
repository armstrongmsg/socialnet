package com.armstrongmsg.socialnet.storage.database.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;
import com.armstrongmsg.socialnet.util.PersistenceTest;

public class DefaultFollowRepositoryTest extends PersistenceTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_ID_2 = "userId2";
	private static final String USER_NAME_1 = "username1";
	private static final String USER_NAME_2 = "username2";
	private static final String USER_DESCRIPTION_1 = "userDescription1";
	private static final String USER_DESCRIPTION_2 = "userDescription2";
	private static final String PASSWORD_1 = "password1";
	private static final String PASSWORD_2 = "password2";
	private UserRepository userRepository;
	private FollowRepository followRepository;
	private Follow follow1;
	private User follower;
	private User followed;
	private Profile profile1;
	private Profile profile2;
	private DatabaseConnectionManager connectionManager;
	
	@Before
	@Override
	public void setUp() throws FatalErrorException {
		super.setUp();
		
		connectionManager = super.getConnectionManager();
		
		profile1 = new Profile(USER_DESCRIPTION_1, new ArrayList<Post>());
		follower = new User(USER_ID_1, USER_NAME_1, PASSWORD_1, profile1);		
		profile2 = new Profile(USER_DESCRIPTION_2, new ArrayList<Post>());
		followed = new User(USER_ID_2, USER_NAME_2, PASSWORD_2, profile2);
		
		userRepository = new DefaultUserRepository(connectionManager);
		userRepository.saveUser(follower);
		userRepository.saveUser(followed);
		
		connectionManager.close();
	}
	
	@Test
	public void testFollowPersistence() {
		connectionManager = super.getConnectionManager();
		
		follow1 = new Follow(follower, followed);
		followRepository = new DefaultFollowRepository(connectionManager);
		
		List<Follow> followsByUser1 = followRepository.getFollowsByUserId(USER_ID_1);
		List<Follow> followsByUser2 = followRepository.getFollowsByUserId(USER_ID_2);
		
		assertTrue(followsByUser1.isEmpty());
		assertTrue(followsByUser2.isEmpty());
		
		followRepository.saveFollow(follow1);
		
		// Shutdown connections
		connectionManager.close();
				
		// Restart db connection
		connectionManager = super.getConnectionManager();
		followRepository = new DefaultFollowRepository(connectionManager);
		
		List<Follow> followsAfterSaveByUser1 = followRepository.getFollowsByUserId(USER_ID_1);
		List<Follow> followsAfterSaveByUser2 = followRepository.getFollowsByUserId(USER_ID_2);
		
		assertEquals(1, followsAfterSaveByUser1.size());
		assertEquals(follower, followsAfterSaveByUser1.get(0).getFollower());
		assertEquals(followed, followsAfterSaveByUser1.get(0).getFollowed());
		
		assertEquals(1, followsAfterSaveByUser2.size());
		assertEquals(follower, followsAfterSaveByUser2.get(0).getFollower());
		assertEquals(followed, followsAfterSaveByUser2.get(0).getFollowed());
		
		followRepository.removeFollow(follow1);
		
		// Shutdown connections
		connectionManager.close();
						
		// Restart db connection
		connectionManager = super.getConnectionManager();
		followRepository = new DefaultFollowRepository(connectionManager);
		
		List<Follow> followsAfterRemovalByUser1 = followRepository.getFollowsByUserId(USER_ID_1);
		List<Follow> followsAfterRemovalByUser2 = followRepository.getFollowsByUserId(USER_ID_2);
		assertTrue(followsAfterRemovalByUser1.isEmpty());
		assertTrue(followsAfterRemovalByUser2.isEmpty());
		
		connectionManager.close();
	}
	
	@After
	@Override
	public void tearDown() throws IOException {
		super.tearDown();
	}
}
