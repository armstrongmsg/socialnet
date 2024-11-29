package com.armstrongmsg.socialnet.storage.database.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;
import com.armstrongmsg.socialnet.util.PersistenceTest;

public class DefaultUserRepositoryTest extends PersistenceTest {
	private static final String USER_ID_1 = "userId1";
	private static final String USER_NAME_1 = "username1";
	private static final String UPDATED_USER_NAME_1 = "updatedUsername1";
	private static final String PASSWORD_1 = "password1";
	private static final String UPDATED_PASSWORD_1 = "updatedPassword1";
	private static final String USER_DESCRIPTION_1 = "userDescription1";
	private static final String UPDATED_USER_DESCRIPTION_1 = "updatedUserDescription1";
	private static final String POST_TITLE_1 = "postTitle1";
	private static final String UPDATED_POST_TITLE_1 = "updatedPostTitle1";
	private static final String UPDATED_POST_TITLE_2 = "updatedPostTitle2";
	private static final long POST_TIMESTAMP_1 = 1;
	private static final long UPDATED_POST_TIMESTAMP_1 = 2;
	private static final long UPDATED_POST_TIMESTAMP_2 = 3;
	private static final String POST_CONTENT_1 = "postContent1";
	private static final String UPDATED_POST_CONTENT_1 = "updatedPostContent1";
	private static final String UPDATED_POST_CONTENT_2 = "updatedPostContent2";
	private static final PostVisibility POST_VISIBILITY_1 = PostVisibility.PRIVATE;
	private static final PostVisibility UPDATED_POST_VISIBILITY_1 = PostVisibility.PUBLIC;
	private static final PostVisibility UPDATED_POST_VISIBILITY_2 = PostVisibility.PRIVATE;
	private static final String POST_PICTURE_ID = "pictureId";
	private static final String UPDATED_POST_PICTURE_ID = "updatedPictureId";
	private static final String UPDATED_POST_PICTURE_ID_2 = "updatedPictureId2";
	private static final String PROFILE_PIC_ID = "profilePicId";
	private static final String UPDATED_PROFILE_PIC_ID = "updatedProfilePicId";
	private DefaultUserRepository repository;
	private User user;
	private Profile profile1;
	private List<Post> posts;
	
	@Test
	public void testUserPersistence() {
		DatabaseConnectionManager connectionManager = super.getConnectionManager(); 
		repository = new DefaultUserRepository(connectionManager);

		// Initially, database is empty
		assertTrue(repository.getAllUsers().isEmpty());

		// Create a new user
		Post post = new Post(POST_TITLE_1, POST_TIMESTAMP_1, POST_CONTENT_1, POST_VISIBILITY_1, Arrays.asList(POST_PICTURE_ID));
		posts = Arrays.asList(post);
		profile1 = new Profile(USER_DESCRIPTION_1, posts);
		profile1.setProfilePicId(PROFILE_PIC_ID);
		user = new User(USER_ID_1, USER_NAME_1, PASSWORD_1, profile1);

		// Save the new user
		repository.saveUser(user);
		
		// Shutdown connections
		connectionManager.close();
		
		// Restart db connection
		connectionManager = super.getConnectionManager();
		repository = new DefaultUserRepository(connectionManager);
		
		// Retrieve user by user id and user name 
		List<User> users = repository.getAllUsers();
		
		assertEquals(1, users.size());
		assertEquals(USER_ID_1, users.get(0).getUserId());
		assertEquals(USER_NAME_1, users.get(0).getUsername());
		assertEquals(PASSWORD_1, users.get(0).getPassword());
		assertEquals(USER_DESCRIPTION_1, users.get(0).getProfile().getDescription());
		assertEquals(PROFILE_PIC_ID, users.get(0).getProfile().getProfilePicId());
		List<Post> userPosts = users.get(0).getProfile().getPosts();
		assertEquals(1, userPosts.size());
		assertEquals(POST_TITLE_1, userPosts.get(0).getTitle());
		assertEquals(POST_TIMESTAMP_1, userPosts.get(0).getTimestamp());
		assertEquals(POST_CONTENT_1, userPosts.get(0).getContent());
		assertEquals(POST_VISIBILITY_1, userPosts.get(0).getVisibility());
		assertEquals(POST_PICTURE_ID, userPosts.get(0).getMediaIds().get(0));
		
		User retrievedUserByUserId = repository.getUserById(USER_ID_1);
		assertEquals(USER_ID_1, retrievedUserByUserId.getUserId());
		assertEquals(USER_NAME_1, retrievedUserByUserId.getUsername());
		assertEquals(PASSWORD_1, retrievedUserByUserId.getPassword());
		assertEquals(USER_DESCRIPTION_1, retrievedUserByUserId.getProfile().getDescription());
		assertEquals(PROFILE_PIC_ID, retrievedUserByUserId.getProfile().getProfilePicId());
		List<Post> retrievedUserByIdPosts = users.get(0).getProfile().getPosts();
		assertEquals(1, retrievedUserByIdPosts.size());
		assertEquals(POST_TITLE_1, retrievedUserByIdPosts.get(0).getTitle());
		assertEquals(POST_TIMESTAMP_1, retrievedUserByIdPosts.get(0).getTimestamp());
		assertEquals(POST_CONTENT_1, retrievedUserByIdPosts.get(0).getContent());
		assertEquals(POST_VISIBILITY_1, retrievedUserByIdPosts.get(0).getVisibility());
		assertEquals(POST_PICTURE_ID, userPosts.get(0).getMediaIds().get(0));
		
		User retrievedUserByUsername = repository.getUserByUsername(USER_NAME_1);
		assertEquals(USER_ID_1, retrievedUserByUsername.getUserId());
		assertEquals(USER_NAME_1, retrievedUserByUsername.getUsername());
		assertEquals(PASSWORD_1, retrievedUserByUsername.getPassword());
		assertEquals(USER_DESCRIPTION_1, retrievedUserByUsername.getProfile().getDescription());
		assertEquals(PROFILE_PIC_ID, retrievedUserByUsername.getProfile().getProfilePicId());
		List<Post> retrievedUserByUsernamePosts = users.get(0).getProfile().getPosts();
		assertEquals(1, retrievedUserByUsernamePosts.size());
		assertEquals(POST_TITLE_1, retrievedUserByUsernamePosts.get(0).getTitle());
		assertEquals(POST_TIMESTAMP_1, retrievedUserByUsernamePosts.get(0).getTimestamp());
		assertEquals(POST_CONTENT_1, retrievedUserByUsernamePosts.get(0).getContent());
		assertEquals(POST_VISIBILITY_1, retrievedUserByUsernamePosts.get(0).getVisibility());
		assertEquals(POST_PICTURE_ID, userPosts.get(0).getMediaIds().get(0));
		
		// Update user and posts
		Post updatedPost = new Post(UPDATED_POST_TITLE_1, UPDATED_POST_TIMESTAMP_1, 
				UPDATED_POST_CONTENT_1, UPDATED_POST_VISIBILITY_1, Arrays.asList(UPDATED_POST_PICTURE_ID));
		updatedPost.setId(post.getId());
		List<Post> updatedPosts = Arrays.asList(updatedPost);
		Profile updatedProfile1 = new Profile(UPDATED_USER_DESCRIPTION_1, updatedPosts);
		updatedProfile1.setProfilePicId(UPDATED_PROFILE_PIC_ID);
		User updatedUser = new User(USER_ID_1, UPDATED_USER_NAME_1, UPDATED_PASSWORD_1, updatedProfile1);
		
		// Save user
		repository.updateUser(updatedUser);
		
		// Shutdown
		connectionManager.close();
						
		// Restart db connection
		connectionManager = super.getConnectionManager();
		repository = new DefaultUserRepository(connectionManager);
		
		// User data is updated correctly
		User userAfterUpdate1 = repository.getUserById(USER_ID_1);
		assertEquals(USER_ID_1, userAfterUpdate1.getUserId());
		assertEquals(UPDATED_USER_NAME_1, userAfterUpdate1.getUsername());
		assertEquals(UPDATED_PASSWORD_1, userAfterUpdate1.getPassword());
		assertEquals(UPDATED_USER_DESCRIPTION_1, userAfterUpdate1.getProfile().getDescription());
		assertEquals(UPDATED_PROFILE_PIC_ID, userAfterUpdate1.getProfile().getProfilePicId());
		List<Post> userAfterUpdate1Posts = userAfterUpdate1.getProfile().getPosts();
		assertEquals(1, userAfterUpdate1Posts.size());
		assertEquals(UPDATED_POST_TITLE_1, userAfterUpdate1Posts.get(0).getTitle());
		assertEquals(UPDATED_POST_TIMESTAMP_1, userAfterUpdate1Posts.get(0).getTimestamp());
		assertEquals(UPDATED_POST_CONTENT_1, userAfterUpdate1Posts.get(0).getContent());
		assertEquals(UPDATED_POST_VISIBILITY_1, userAfterUpdate1Posts.get(0).getVisibility());
		assertEquals(UPDATED_POST_PICTURE_ID, userAfterUpdate1Posts.get(0).getMediaIds().get(0));
		
		// Updated only post data
		Post updatedPost2 = new Post(UPDATED_POST_TITLE_2, UPDATED_POST_TIMESTAMP_2, 
				UPDATED_POST_CONTENT_2, UPDATED_POST_VISIBILITY_2, Arrays.asList(UPDATED_POST_PICTURE_ID_2));
		updatedPost.setId(post.getId());
		List<Post> updatedPosts2 = Arrays.asList(updatedPost2);
		Profile updatedProfile2 = new Profile(UPDATED_USER_DESCRIPTION_1, updatedPosts2);
		updatedProfile2.setProfilePicId(UPDATED_PROFILE_PIC_ID);
		User updatedUser2 = new User(USER_ID_1, UPDATED_USER_NAME_1, UPDATED_PASSWORD_1, updatedProfile2);
		
		// Save user
		repository.updateUser(updatedUser2);
		
		// Shutdown
		connectionManager.close();
		
		// Restart db connection
		connectionManager = super.getConnectionManager();
		repository = new DefaultUserRepository(connectionManager);
		
		// Post data is updated correctly
		User userAfterUpdate2 = repository.getUserById(USER_ID_1);
		assertEquals(USER_ID_1, userAfterUpdate2.getUserId());
		assertEquals(UPDATED_USER_NAME_1, userAfterUpdate2.getUsername());
		assertEquals(UPDATED_PASSWORD_1, userAfterUpdate2.getPassword());
		assertEquals(UPDATED_USER_DESCRIPTION_1, userAfterUpdate2.getProfile().getDescription());
		assertEquals(UPDATED_PROFILE_PIC_ID, userAfterUpdate2.getProfile().getProfilePicId());
		List<Post> userAfterUpdate2Posts = userAfterUpdate2.getProfile().getPosts();
		assertEquals(1, userAfterUpdate2Posts.size());
		assertEquals(UPDATED_POST_TITLE_2, userAfterUpdate2Posts.get(0).getTitle());
		assertEquals(UPDATED_POST_TIMESTAMP_2, userAfterUpdate2Posts.get(0).getTimestamp());
		assertEquals(UPDATED_POST_CONTENT_2, userAfterUpdate2Posts.get(0).getContent());
		assertEquals(UPDATED_POST_VISIBILITY_2, userAfterUpdate2Posts.get(0).getVisibility());
		assertEquals(UPDATED_POST_PICTURE_ID_2, userAfterUpdate2Posts.get(0).getMediaIds().get(0));
		
		// Remove user
		repository.removeUserById(USER_ID_1);
		
		// Shutdown
		connectionManager.close();
				
		// Restart db connection
		connectionManager = super.getConnectionManager();
		repository = new DefaultUserRepository(connectionManager);
		
		// User is removed correctly
		assertTrue(repository.getAllUsers().isEmpty());
		assertNull(repository.getUserById(USER_ID_1));
		
		connectionManager.close();
	}
	
	@After
	public void tearDown() throws IOException {
		super.tearDown();
	}
}
