package com.armstrongmsg.socialnet.storage.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.User;

public class LruCacheTest {
	private static final int TOTAL_CAPACITY = 2;
	private static final String USER_ID_1 = "userId1";
	private static final String USERNAME_1 = "username1";
	private static final String PASSWORD_1 = "password1";
	private static final String USER_ID_2 = "userId2";
	private static final String USERNAME_2 = "username2";
	private static final String PASSWORD_2 = "password2";
	private static final String USER_ID_3 = "userId3";
	private static final String USERNAME_3 = "username3";
	private static final String PASSWORD_3 = "password3";
	private User user1;
	private User user2;
	private User user3;
	private LruCache cache;
	
	@Before
	public void setUp() {
		user1 = new User(USER_ID_1, USERNAME_1, PASSWORD_1, null);
		user2 = new User(USER_ID_2, USERNAME_2, PASSWORD_2, null);
		user3 = new User(USER_ID_3, USERNAME_3, PASSWORD_3, null);
		
		cache = new LruCache(TOTAL_CAPACITY);
	}
	
	@Test
	public void testPutAndGetUserById() throws FatalErrorException, UserAlreadyExistsException, UserNotFoundException {
		cache.putUser(user1);
		cache.putUser(user2);

		assertEquals(user2, cache.getUserById(USER_ID_2));
		assertEquals(user1, cache.getUserById(USER_ID_1));
		
		try {
			cache.getUserById(USER_ID_3);
			fail("Expected exception.");
		} catch (UserNotFoundException e) {
			
		}
		
		cache.putUser(user3);
		
		assertEquals(user1, cache.getUserById(USER_ID_1));
		assertEquals(user3, cache.getUserById(USER_ID_3));
		
		try {
			cache.getUserById(USER_ID_2);
			fail("Expected exception.");
		} catch (UserNotFoundException e) {
			
		}
	}
	
	@Test
	public void testPutAndGetUserByUsername() throws FatalErrorException, UserAlreadyExistsException, UserNotFoundException {
		cache.putUser(user1);
		cache.putUser(user2);

		assertEquals(user2, cache.getUserByUsername(USERNAME_2));
		assertEquals(user1, cache.getUserByUsername(USERNAME_1));
		
		try {
			cache.getUserByUsername(USERNAME_3);
			fail("Expected exception.");
		} catch (UserNotFoundException e) {
			
		}
		
		cache.putUser(user3);
		
		assertEquals(user1, cache.getUserByUsername(USERNAME_1));
		assertEquals(user3, cache.getUserByUsername(USERNAME_3));
		
		try {
			cache.getUserByUsername(USERNAME_2);
			fail("Expected exception.");
		} catch (UserNotFoundException e) {
			
		}
	}
}
