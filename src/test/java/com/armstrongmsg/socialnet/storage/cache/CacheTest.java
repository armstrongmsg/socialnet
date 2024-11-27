package com.armstrongmsg.socialnet.storage.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
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
import com.armstrongmsg.socialnet.util.ClassFactory;

@RunWith(Parameterized.class)
public class CacheTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_ID_2 = "userId2";
	private static final String USER_NAME_1 = "username1";
	private static final String USER_NAME_2 = "username2";
	private static final String PASSWORD_1 = "password1";
	private static final String UPDATED_PASSWORD_1 = "updatedPassword1";
	private Cache cache;
	private User user1;
	private User user1Copy;
	private User updatedUser1;
	private User user2;
	private Friendship friendship;
	private Friendship friendshipCopy;
	private Follow follow;
	private Follow followCopy;
	private FriendshipRequest friendshipRequest;
	private FriendshipRequest friendshipRequestCopy;
	private String cacheType;
	
	@Parameterized.Parameters
	public static List<Object[]> getTestParameters() throws FatalErrorException {
		List<Object[]> args = new ArrayList<Object[]>();
		args.add(new Object[] {DefaultCache.class.getCanonicalName()});
		args.add(new Object[] {LruCache.class.getCanonicalName()});
		return args;
	}
	
	public CacheTest(String cacheType) throws FatalErrorException {
		this.cacheType = cacheType;
	}
	
	@Before
	public void setUp() throws FatalErrorException {
		this.cache = (Cache) new ClassFactory().createInstance(cacheType);
		user1 = new User(USER_ID_1, USER_NAME_1, PASSWORD_1, null);
		user2 = new User(USER_ID_2, USER_NAME_2, null, null);
		user1Copy = new User(USER_ID_1, USER_NAME_1, PASSWORD_1, null);
		updatedUser1 = new User(USER_ID_1, USER_NAME_1, UPDATED_PASSWORD_1, null);
		friendship = new Friendship(user1, user2);
		friendshipCopy = new Friendship(friendship.getId(), user1, user2);
		follow = new Follow(user1, user2);
		followCopy = new Follow(follow.getId(), user1, user2);
		friendshipRequest = new FriendshipRequest(user1, user2);
		friendshipRequestCopy = new FriendshipRequest(friendshipRequest.getId(), user1, user2);
	}
	
	@Test
	public void testGetPutAndRemoveUser() throws UserNotFoundException, InternalErrorException, UserAlreadyExistsException {
		assertThrows(UserNotFoundException.class, () -> cache.getUserById(USER_ID_1));
		assertThrows(UserNotFoundException.class, () -> cache.getUserById(USER_ID_2));
		assertThrows(UserNotFoundException.class, () -> cache.getUserByUsername(USER_NAME_1));
		assertThrows(UserNotFoundException.class, () -> cache.getUserByUsername(USER_NAME_2));
		
		cache.putUser(user1);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertThrows(UserNotFoundException.class, () -> cache.getUserById(USER_ID_2));
		assertThrows(UserNotFoundException.class, () -> cache.getUserByUsername(USER_NAME_2));
		
		assertThrows(UserAlreadyExistsException.class, () -> cache.putUser(user1));
		assertThrows(UserAlreadyExistsException.class, () -> cache.putUser(user1Copy));
		
		cache.putUser(user2);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertEquals(user2, cache.getUserByUsername(USER_NAME_2));
		
		cache.removeUserById(USER_ID_1);
		
		assertThrows(UserNotFoundException.class, () -> cache.getUserById(USER_ID_1));
		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertThrows(UserNotFoundException.class, () -> cache.getUserByUsername(USER_NAME_1));
		assertEquals(user2, cache.getUserByUsername(USER_NAME_2));
		
		assertThrows(UserNotFoundException.class, () -> cache.removeUserById(USER_ID_1));
	}
	
	@Test
	public void testUpdateUser() throws UserAlreadyExistsException, InternalErrorException, UserNotFoundException {
		cache.putUser(user1);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user1, cache.getUserByUsername(USER_NAME_1));
		assertEquals(PASSWORD_1, cache.getUserById(USER_ID_1).getPassword());
		assertEquals(PASSWORD_1, cache.getUserByUsername(USER_NAME_1).getPassword());
		
		cache.updateUser(updatedUser1);
		
		assertEquals(updatedUser1, cache.getUserById(USER_ID_1));
		assertEquals(updatedUser1, cache.getUserByUsername(USER_NAME_1));
		assertEquals(UPDATED_PASSWORD_1, cache.getUserById(USER_ID_1).getPassword());
		assertEquals(UPDATED_PASSWORD_1, cache.getUserByUsername(USER_NAME_1).getPassword());
	}
	
	@Test
	public void testGetPutAndRemoveFriendship() throws InternalErrorException, FriendshipAlreadyExistsException, FriendshipNotFoundException {		
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
		
		assertThrows(FriendshipAlreadyExistsException.class, () -> cache.putFriendship(friendshipCopy));

		cache.removeFriendship(friendship);
		
		assertTrue(cache.getFriendshipsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFriendshipsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFriendshipsByUsername(USER_NAME_2).isEmpty());
		
		assertThrows(FriendshipNotFoundException.class, () -> cache.removeFriendship(friendshipCopy));
	}
	
	@Test
	public void testGetPutAndRemoveFollow() throws InternalErrorException, FollowAlreadyExistsException, FollowNotFoundException {
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
		
		assertThrows(FollowAlreadyExistsException.class, () -> cache.putFollow(followCopy));
				
		cache.removeFollow(follow);
		
		assertTrue(cache.getFollowsByUserId(USER_ID_1).isEmpty());
		assertTrue(cache.getFollowsByUserId(USER_ID_2).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_1).isEmpty());
		assertTrue(cache.getFollowsByUsername(USER_NAME_2).isEmpty());
		
		assertThrows(FollowNotFoundException.class, () -> cache.removeFollow(follow));
	}
	
	@Test
	public void testGetPutAndRemoveFriendshipRequest() throws InternalErrorException, FriendshipRequestAlreadyExistsException, FriendshipRequestNotFound {
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_2).isEmpty());
		assertThrows(FriendshipRequestNotFound.class, () -> cache.getReceivedFriendshipRequestById(USER_ID_1, USER_NAME_2));
		
		cache.putFriendshipRequest(friendshipRequest);
		
		assertEquals(user1, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequester());
		assertEquals(user2, cache.getSentFriendshipRequestsById(USER_ID_1).get(0).getRequested());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertEquals(user1, cache.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1).getRequester());
		assertEquals(user2, cache.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1).getRequested());
		assertEquals(user1, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequester());
		assertEquals(user2, cache.getReceivedFriendshipRequestsById(USER_ID_2).get(0).getRequested()); 
		
		assertThrows(FriendshipRequestAlreadyExistsException.class, () -> cache.putFriendshipRequest(friendshipRequestCopy));
		
		cache.removeFriendshipRequestById(friendshipRequest);
		
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getSentFriendshipRequestsById(USER_ID_2).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_1).isEmpty());
		assertTrue(cache.getReceivedFriendshipRequestsById(USER_ID_2).isEmpty());
		
		assertThrows(FriendshipRequestNotFound.class, () -> cache.removeFriendshipRequestById(friendshipRequest));
		assertThrows(FriendshipRequestNotFound.class, () -> cache.getReceivedFriendshipRequestById(USER_ID_2, USER_NAME_1));
	}
}
