package com.armstrongmsg.socialnet.storage.database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

public class InMemoryDatabaseManagerTest {
	private static final String USER_ID_1 = "USER_ID_1";
	private static final String USERNAME_1 = "USERNAME_1";
	private static final String PASSWORD_1 = "PASSWORD_1";
	private static final String USER_ID_2 = "USER_ID_2";
	private static final String USERNAME_2 = "USERNAME_2";
	private static final String PASSWORD_2 = "PASSWORD_2";
	private static final String USER_ID_3 = "USER_ID_3";
	private static final String USERNAME_3 = "USERNAME_3";
	private static final String PASSWORD_3 = "PASSWORD_3";
	private static final String UPDATED_PASSWORD_1 = "UPDATED_PASSWORD_1";
	private InMemoryDatabaseManager manager;
	private User user1;
	private User user2;
	private User user3;
	private User updatedUser1;
	
	@Before
	public void setUp() {
		user1 = new User(USER_ID_1, USERNAME_1, PASSWORD_1, null);
		user2 = new User(USER_ID_2, USERNAME_2, PASSWORD_2, null);
		user3 = new User(USER_ID_3, USERNAME_3, PASSWORD_3, null);
		updatedUser1 = new User(USER_ID_1, USERNAME_1, UPDATED_PASSWORD_1, null);
		
		manager = new InMemoryDatabaseManager();
	}
	
	@Test
	public void testUserSaveAndRetrieval() throws UserNotFoundException {
		List<User> usersBeforeSave = manager.getAllUsers();
		assertEquals(0, usersBeforeSave.size());
		
		manager.saveUser(user1);
		manager.saveUser(user2);
		
		List<User> usersAfterSave = manager.getAllUsers();
		assertEquals(2, usersAfterSave.size());
		
		assertEquals(user1, manager.getUserById(USER_ID_1));
		assertEquals(user2, manager.getUserById(USER_ID_2));
		assertEquals(user1, manager.getUserByUsername(USERNAME_1));
		assertEquals(user2, manager.getUserByUsername(USERNAME_2));
	}
	
	@Test
	public void testUserUpdate() throws UserNotFoundException {
		manager.saveUser(user1);
		manager.updateUser(updatedUser1);
		
		assertEquals(UPDATED_PASSWORD_1, manager.getUserById(USER_ID_1).getPassword());
	}
	
	@Test
	public void testFriendshipSaveRetrievalAndRemoval() {
		Friendship friendship1 = new Friendship(user1, user2);
		Friendship friendship2 = new Friendship(user2, user3);
		
		manager.saveFriendship(friendship1);
		manager.saveFriendship(friendship2);
		
		List<Friendship> user1Friendships = manager.getFriendshipsByUserId(USER_ID_1);
		List<Friendship> user2Friendships = manager.getFriendshipsByUserId(USER_ID_2);
		List<Friendship> user3Friendships = manager.getFriendshipsByUserId(USER_ID_3);
		
		assertEquals(1, user1Friendships.size());
		assertTrue(user1Friendships.contains(friendship1));
		
		assertEquals(2, user2Friendships.size());
		assertTrue(user2Friendships.contains(friendship1));
		assertTrue(user2Friendships.contains(friendship2));
		
		assertEquals(1, user3Friendships.size());
		assertTrue(user3Friendships.contains(friendship2));
		
		manager.removeFriendship(friendship1);
		
		List<Friendship> user1FriendshipsAfterRemoval = manager.getFriendshipsByUserId(USER_ID_1);
		List<Friendship> user2FriendshipsAfterRemoval = manager.getFriendshipsByUserId(USER_ID_2);
		List<Friendship> user3FriendshipsAfterRemoval = manager.getFriendshipsByUserId(USER_ID_3);
		
		assertTrue(user1FriendshipsAfterRemoval.isEmpty());
		
		assertEquals(1, user2FriendshipsAfterRemoval.size());
		assertTrue(user2FriendshipsAfterRemoval.contains(friendship2));
		
		assertEquals(1, user3FriendshipsAfterRemoval.size());
		assertTrue(user3FriendshipsAfterRemoval.contains(friendship2));
	}
	
	@Test
	public void testFollowSaveRetrievalAndRemoval() {
		Follow follow1 = new Follow(user1, user2);
		Follow follow2 = new Follow(user2, user3);
		
		manager.saveFollow(follow1);
		manager.saveFollow(follow2);
		
		List<Follow> user1Follows = manager.getFollowsByUserId(USER_ID_1);
		List<Follow> user2Follows = manager.getFollowsByUserId(USER_ID_2);
		List<Follow> user3Follows = manager.getFollowsByUserId(USER_ID_3);
		
		assertEquals(1, user1Follows.size());
		assertTrue(user1Follows.contains(follow1));
		
		assertEquals(2, user2Follows.size());
		assertTrue(user2Follows.contains(follow1));
		assertTrue(user2Follows.contains(follow2));
		
		assertEquals(1, user3Follows.size());
		assertTrue(user3Follows.contains(follow2));
		
		manager.removeFollow(follow1);
		
		List<Follow> user1FollowsAfterRemoval = manager.getFollowsByUserId(USER_ID_1);
		List<Follow> user2FollowsAfterRemoval = manager.getFollowsByUserId(USER_ID_2);
		List<Follow> user3FollowsAfterRemoval = manager.getFollowsByUserId(USER_ID_3);
		
		assertTrue(user1FollowsAfterRemoval.isEmpty());
		
		assertEquals(1, user2FollowsAfterRemoval.size());
		assertTrue(user2FollowsAfterRemoval.contains(follow2));
		
		assertEquals(1, user3FollowsAfterRemoval.size());
		assertTrue(user3FollowsAfterRemoval.contains(follow2));
	}
	
	@Test
	public void testFriendshipRequestSaveRetrievalAndRemoval() {
		FriendshipRequest request1 = new FriendshipRequest(user1, user2);
		FriendshipRequest request2 = new FriendshipRequest(user2, user3);
		FriendshipRequest request3 = new FriendshipRequest(user1, user3);
		
		manager.saveFriendshipRequest(request1);
		manager.saveFriendshipRequest(request2);
		manager.saveFriendshipRequest(request3);
		
		List<FriendshipRequest> receivedRequestsUser1 = manager.getReceivedFriendshipRequestsById(USER_ID_1);
		assertTrue(receivedRequestsUser1.isEmpty());
		
		List<FriendshipRequest> receivedRequestsUser2 = manager.getReceivedFriendshipRequestsById(USER_ID_2);
		assertEquals(1, receivedRequestsUser2.size());
		assertTrue(receivedRequestsUser2.contains(request1));
		
		List<FriendshipRequest> receivedRequestsUser3 = manager.getReceivedFriendshipRequestsById(USER_ID_3);
		assertEquals(2, receivedRequestsUser3.size());
		assertTrue(receivedRequestsUser3.contains(request2));
		assertTrue(receivedRequestsUser3.contains(request3));
		
		List<FriendshipRequest> sentRequestsUser1 = manager.getSentFriendshipRequestsById(USER_ID_1);
		assertEquals(2, sentRequestsUser1.size());
		assertTrue(sentRequestsUser1.contains(request1));
		assertTrue(sentRequestsUser1.contains(request3));
		
		List<FriendshipRequest> sentRequestsUser2 = manager.getSentFriendshipRequestsById(USER_ID_2);
		assertEquals(1, sentRequestsUser2.size());
		assertTrue(sentRequestsUser2.contains(request2));
		
		List<FriendshipRequest> sentRequestsUser3 = manager.getSentFriendshipRequestsById(USER_ID_3);
		assertTrue(sentRequestsUser3.isEmpty());
		
		assertEquals(request1, manager.getReceivedFriendshipRequestById(USER_ID_2, USERNAME_1));
		assertEquals(request2, manager.getReceivedFriendshipRequestById(USER_ID_3, USERNAME_2));
		assertEquals(request3, manager.getReceivedFriendshipRequestById(USER_ID_3, USERNAME_1));
		
		manager.removeFriendshipRequestById(request1);
		
		List<FriendshipRequest> receivedRequestsUser1AfterRemoval = manager.getReceivedFriendshipRequestsById(USER_ID_1);
		assertTrue(receivedRequestsUser1AfterRemoval.isEmpty());
		
		List<FriendshipRequest> receivedRequestsUser2AfterRemoval = manager.getReceivedFriendshipRequestsById(USER_ID_2);
		assertTrue(receivedRequestsUser2AfterRemoval.isEmpty());
		
		List<FriendshipRequest> receivedRequestsUser3AfterRemoval = manager.getReceivedFriendshipRequestsById(USER_ID_3);
		assertEquals(2, receivedRequestsUser3AfterRemoval.size());
		assertTrue(receivedRequestsUser3AfterRemoval.contains(request2));
		assertTrue(receivedRequestsUser3AfterRemoval.contains(request3));
		
		List<FriendshipRequest> sentRequestsUser1AfterRemoval = manager.getSentFriendshipRequestsById(USER_ID_1);
		assertEquals(1, sentRequestsUser1AfterRemoval.size());
		assertTrue(sentRequestsUser1AfterRemoval.contains(request3));
		
		List<FriendshipRequest> sentRequestsUser2AfterRemoval = manager.getSentFriendshipRequestsById(USER_ID_2);
		assertEquals(1, sentRequestsUser2AfterRemoval.size());
		assertTrue(sentRequestsUser2AfterRemoval.contains(request2));
		
		List<FriendshipRequest> sentRequestsUser3AfterRemoval = manager.getSentFriendshipRequestsById(USER_ID_3);
		assertTrue(sentRequestsUser3AfterRemoval.isEmpty());
	}
}
