package com.armstrongmsg.socialnet.storage.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Follow;
import com.armstrongmsg.socialnet.model.Friendship;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.model.User;

@RunWith(Parameterized.class)
public class CacheTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_ID_2 = "userId2";
	private static final String USER_NAME_1 = "username1";
	private static final String USER_NAME_2 = "username2";
	private Cache cache;
	private User user1;
	private User user1Copy;
	private User user2;
	private Friendship friendship;
	private Friendship friendshipCopy;
	private Follow follow;
	private Follow followCopy;
	private FriendshipRequest friendshipRequest;
	private FriendshipRequest friendshipRequestCopy;
	
	@Parameterized.Parameters
	public static List<Object[]> getTestParameters() throws FatalErrorException {
		List<Object[]> args = new ArrayList<Object[]>();
		args.add(new Object[] {new DefaultCache()});
		args.add(new Object[] {new LruCache()});
		return args;
	}
	
	public CacheTest(Cache cache) throws FatalErrorException {
		this.cache = cache;
	}
	
	@Before
	public void setUp() {
		user1 = new User(USER_ID_1, USER_NAME_1, null, null);
		user2 = new User(USER_ID_2, USER_NAME_2, null, null);
		user1Copy = new User(USER_ID_1, USER_NAME_1, null, null);
		friendship = new Friendship(user1, user2);
		friendshipCopy = new Friendship(friendship.getId(), user1, user2);
		follow = new Follow(user1, user2);
		followCopy = new Follow(follow.getId(), user1, user2);
		friendshipRequest = new FriendshipRequest(user1, user2);
		friendshipRequestCopy = new FriendshipRequest(friendshipRequest.getId(), user1, user2);
	}
	
	@Test
	public void testGetPutAndRemoveUser() {
		assertNull(cache.getUserById(USER_ID_1));
		assertNull(cache.getUserById(USER_ID_2));
		assertNull(cache.getUserByUsername(USER_NAME_1));
		assertNull(cache.getUserByUsername(USER_NAME_2));
		
		cache.putUser(user1);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertNull(cache.getUserById(USER_ID_2));
		assertNull(cache.getUserByUsername(USER_NAME_2));
		
		cache.putUser(user2);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertEquals(user2, cache.getUserByUsername(USER_NAME_2));
		
		cache.putUser(user1Copy);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertEquals(user2, cache.getUserByUsername(USER_NAME_2));
		
		cache.removeUserById(USER_ID_1);
		
		assertNull(cache.getUserById(USER_ID_1));
		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertNull(cache.getUserByUsername(USER_NAME_1));
		assertEquals(user2, cache.getUserByUsername(USER_NAME_2));
	}
	
	@Test
	public void testGetPutAndRemoveFriendship() {		
		assertTrue(cache.getFriendshipsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFriendshipsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_2).isEmpty());
		
		cache.putFriendship(friendship);
		
		assertEquals(user1, cache.getFriendshipsByUserId(USER_ID_1).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUserId(USER_ID_1).get(0).getFriend2());
		assertEquals(user1, cache.getFriendshipsByUserId(USER_ID_2).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUserId(USER_ID_2).get(0).getFriend2());
		
		assertEquals(user1, cache.getFriendshipsByUsername(USER_NAME_1).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUsername(USER_NAME_1).get(0).getFriend2());
		assertEquals(user1, cache.getFriendshipsByUsername(USER_NAME_2).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUsername(USER_NAME_2).get(0).getFriend2()); 
		
		cache.putFriendship(friendshipCopy);
		
		assertEquals(user1, cache.getFriendshipsByUserId(USER_ID_1).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUserId(USER_ID_1).get(0).getFriend2());
		assertEquals(user1, cache.getFriendshipsByUserId(USER_ID_2).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUserId(USER_ID_2).get(0).getFriend2());
		
		assertEquals(user1, cache.getFriendshipsByUsername(USER_NAME_1).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUsername(USER_NAME_1).get(0).getFriend2());
		assertEquals(user1, cache.getFriendshipsByUsername(USER_NAME_2).get(0).getFriend1());
		assertEquals(user2, cache.getFriendshipsByUsername(USER_NAME_2).get(0).getFriend2());
		
		cache.removeFriendship(friendship);
		
		assertTrue(cache.getFriendshipsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFriendshipsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_2).isEmpty());
	}
	
	@Test
	public void testGetPutAndRemoveFollow() {
		assertTrue(cache.getFollowsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFollowsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_2).isEmpty());
		
		cache.putFollow(follow);
		
		assertEquals(user1, cache.getFollowsByUserId(USER_ID_1).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUserId(USER_ID_1).get(0).getFollowed());
		assertEquals(user1, cache.getFollowsByUserId(USER_ID_2).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUserId(USER_ID_2).get(0).getFollowed());
		
		assertEquals(user1, cache.getFollowsByUsername(USER_NAME_1).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUsername(USER_NAME_1).get(0).getFollowed());
		assertEquals(user1, cache.getFollowsByUsername(USER_NAME_2).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUsername(USER_NAME_2).get(0).getFollowed()); 
		
		cache.putFollow(followCopy);
		
		assertEquals(user1, cache.getFollowsByUserId(USER_ID_1).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUserId(USER_ID_1).get(0).getFollowed());
		assertEquals(user1, cache.getFollowsByUserId(USER_ID_2).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUserId(USER_ID_2).get(0).getFollowed());
		
		assertEquals(user1, cache.getFollowsByUsername(USER_NAME_1).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUsername(USER_NAME_1).get(0).getFollowed());
		assertEquals(user1, cache.getFollowsByUsername(USER_NAME_2).get(0).getFollower());
		assertEquals(user2, cache.getFollowsByUsername(USER_NAME_2).get(0).getFollowed());
		
		cache.removeFollow(follow);
		
		assertTrue(cache.getFollowsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFollowsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_2).isEmpty());
	}
	
	@Test
	public void testGetPutAndRemoveFriendshipRequest() {
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_2).isEmpty());
		
		cache.putFriendshipRequest(friendshipRequest);
		
		assertEquals(user1, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequester());
		assertEquals(user2, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequested());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertEquals(user1, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequester());
		assertEquals(user2, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequested()); 
		
		cache.putFriendshipRequest(friendshipRequestCopy);
		
		assertEquals(user1, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequester());
		assertEquals(user2, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequested());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertEquals(user1, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequester());
		assertEquals(user2, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequested());
		
		cache.removeFriendshipRequestById(friendshipRequest);
		
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_2).isEmpty());
	}
}
