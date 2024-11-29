package com.armstrongmsg.socialnet.storage.database;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.Profile;
import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.repository.FollowRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRepository;
import com.armstrongmsg.socialnet.storage.database.repository.FriendshipRequestRepository;
import com.armstrongmsg.socialnet.storage.database.repository.PictureRepository;
import com.armstrongmsg.socialnet.storage.database.repository.UserRepository;

public class DefaultDatabaseManagerTest {
	private static final String USER_ID = "userId";
	private static final String USER_NAME = "username";
	private static final String PASSWORD = "password";
	private static final String USER_DESCRIPTION = "userDescription";
	private static final String POST_ID_1 = "postId1";
	private static final String POST_ID_2 = "postId2";
	private static final String POST_TITLE_1 = "postTitle1";
	private static final String POST_TITLE_2 = "postTitle2";
	private static final long POST_TIMESTAMP_1 = 1;
	private static final long POST_TIMESTAMP_2 = 2;
	private static final String POST_CONTENT_1 = "postContent1";
	private static final String POST_CONTENT_2 = "postContent2";
	private static final PostVisibility POST_VISIBILITY_1 = PostVisibility.PRIVATE;
	private static final PostVisibility POST_VISIBILITY_2 = PostVisibility.PUBLIC;
	private static final String PICTURE_ID_1 = "pictureId1";
	private static final String PICTURE_ID_2 = "pictureId2";
	private static final String PICTURE_ID_3 = "pictureId3";
	private static final byte[] PICTURE_DATA_1 = {1, 1, 1};
	private static final byte[] PICTURE_DATA_2 = {2, 2, 2};
	private static final byte[] PICTURE_DATA_3 = {3, 3, 3};
	private UserRepository userRepository;
	private FriendshipRepository friendshipRepository;
	private FollowRepository followRepository;
	private FriendshipRequestRepository friendshipRequestsRepository;
	private PictureRepository pictureRepository;
	private DefaultDatabaseManager manager;
	private Picture postPicture1;
	private Post post1;
	private Post post2;
	private User user;
	private List<Post> userPosts;
	private Picture postPicture2;
	private Picture profilePicture;
	private Profile userProfile;
	
	@Before
	public void setUp() {
		postPicture1 = new Picture(PICTURE_ID_1, PICTURE_DATA_1);
		postPicture2 = new Picture(PICTURE_ID_2, PICTURE_DATA_2);
		profilePicture = new Picture(PICTURE_ID_3, PICTURE_DATA_3);
		
		post1 = new Post();
		post1.setId(POST_ID_1);
		post1.setTitle(POST_TITLE_1);
		post1.setTimestamp(POST_TIMESTAMP_1);
		post1.setContent(POST_CONTENT_1);
		post1.setVisibility(POST_VISIBILITY_1);
		post1.setMediaIds(Arrays.asList(PICTURE_ID_1));
		
		post2 = new Post();
		post2.setId(POST_ID_2);
		post2.setTitle(POST_TITLE_2);
		post2.setTimestamp(POST_TIMESTAMP_2);
		post2.setContent(POST_CONTENT_2);
		post2.setVisibility(POST_VISIBILITY_2);
		post2.setMediaIds(Arrays.asList(PICTURE_ID_2));
		
		userPosts = new ArrayList<Post>();
		userPosts.add(post1);
		userPosts.add(post2);
		
		pictureRepository = Mockito.mock(PictureRepository.class);
		Mockito.when(pictureRepository.getPictureById(PICTURE_ID_1)).thenReturn(postPicture1);
		Mockito.when(pictureRepository.getPictureById(PICTURE_ID_2)).thenReturn(postPicture2);
		Mockito.when(pictureRepository.getPictureById(PICTURE_ID_3)).thenReturn(profilePicture);
		
		userProfile = new Profile(USER_DESCRIPTION, userPosts);
		userProfile.setProfilePicId(PICTURE_ID_3);
		
		user = new User(USER_ID, USER_NAME, PASSWORD, userProfile);
		userRepository = Mockito.mock(UserRepository.class);
		Mockito.when(userRepository.getUserById(USER_ID)).thenReturn(user);
		Mockito.when(userRepository.getUserByUsername(USER_NAME)).thenReturn(user);
	}
	
	@Test
	public void testGetUserById() throws UserNotFoundException {
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		User returnedUser = manager.getUserById(USER_ID);
		
		assertEquals(USER_ID, returnedUser.getUserId());
		assertEquals(USER_NAME, returnedUser.getUsername());
		assertEquals(PASSWORD, returnedUser.getPassword());
		assertEquals(USER_DESCRIPTION, returnedUser.getProfile().getDescription());
		assertEquals(2, returnedUser.getProfile().getPosts().size());
		
		Post returnedUserPost1 = returnedUser.getProfile().getPosts().get(0); 
		assertEquals(POST_TITLE_1, returnedUserPost1.getTitle());
		assertEquals(POST_CONTENT_1, returnedUserPost1.getContent());
		assertEquals(POST_TIMESTAMP_1, returnedUserPost1.getTimestamp());
		assertEquals(POST_VISIBILITY_1, returnedUserPost1.getVisibility());
		
		Post returnedUserPost2 = returnedUser.getProfile().getPosts().get(1); 
		assertEquals(POST_TITLE_2, returnedUserPost2.getTitle());
		assertEquals(POST_CONTENT_2, returnedUserPost2.getContent());
		assertEquals(POST_TIMESTAMP_2, returnedUserPost2.getTimestamp());
		assertEquals(POST_VISIBILITY_2, returnedUserPost2.getVisibility());
	}
	
	@Test
	public void testGetUserByUsername() throws UserNotFoundException { 
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		User returnedUser = manager.getUserByUsername(USER_NAME);
		
		assertEquals(USER_ID, returnedUser.getUserId());
		assertEquals(USER_NAME, returnedUser.getUsername());
		assertEquals(PASSWORD, returnedUser.getPassword());
		assertEquals(USER_DESCRIPTION, returnedUser.getProfile().getDescription());
		assertEquals(2, returnedUser.getProfile().getPosts().size());
		
		Post returnedUserPost1 = returnedUser.getProfile().getPosts().get(0); 
		assertEquals(POST_TITLE_1, returnedUserPost1.getTitle());
		assertEquals(POST_CONTENT_1, returnedUserPost1.getContent());
		assertEquals(POST_TIMESTAMP_1, returnedUserPost1.getTimestamp());
		assertEquals(POST_VISIBILITY_1, returnedUserPost1.getVisibility());
		
		Post returnedUserPost2 = returnedUser.getProfile().getPosts().get(1); 
		assertEquals(POST_TITLE_2, returnedUserPost2.getTitle());
		assertEquals(POST_CONTENT_2, returnedUserPost2.getContent());
		assertEquals(POST_TIMESTAMP_2, returnedUserPost2.getTimestamp());
		assertEquals(POST_VISIBILITY_2, returnedUserPost2.getVisibility());
	}
	
	@Test
	public void testSaveUser() {
		userProfile.setProfilePicId(PICTURE_ID_3);
		post1.setMediaIds(Arrays.asList(PICTURE_ID_1));
		post2.setMediaIds(Arrays.asList(PICTURE_ID_2));
		
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		manager.saveUser(user);
		
		Mockito.verify(userRepository).saveUser(user);
	}
	
	@Test
	public void testSaveUserWithDefaultProfilePic() {
		userProfile.setProfilePicId(SystemConstants.DEFAULT_PROFILE_PIC_ID);
		post1.setMediaIds(Arrays.asList(PICTURE_ID_1));
		post2.setMediaIds(Arrays.asList(PICTURE_ID_2));
		
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		manager.saveUser(user);
		
		Mockito.verify(userRepository).saveUser(user);
	}
	
	@Test
	public void testUpdateUser() throws UserNotFoundException {
		userProfile.setProfilePicId(PICTURE_ID_3);
		post1.setMediaIds(Arrays.asList(PICTURE_ID_1));
		post2.setMediaIds(Arrays.asList(PICTURE_ID_2));
		
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		manager.updateUser(user);
		
		Mockito.verify(userRepository).updateUser(user);
	}
	
	@Test
	public void testUpdateUserWithDefaultProfilePic() throws UserNotFoundException {
		userProfile.setProfilePicId(SystemConstants.DEFAULT_PROFILE_PIC_ID);
		post1.setMediaIds(Arrays.asList(PICTURE_ID_1));
		post2.setMediaIds(Arrays.asList(PICTURE_ID_2));
		
		manager = new DefaultDatabaseManager(userRepository, friendshipRepository, 
				followRepository, friendshipRequestsRepository);
		
		manager.updateUser(user);
		
		Mockito.verify(userRepository).updateUser(user);
	}
}
