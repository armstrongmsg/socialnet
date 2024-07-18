package com.armstrongmsg.socialnet.view.jsf.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.model.Profile;

public class JsfConnectorTest {
	private static final String POST_TITLE_1 = "post1";
	private static final String POST_CONTENT_1 = "post content 1";
	private static final long POST_TIMESTAMP_1 = 0;
	private static final PostVisibility POST_VISIBILITY_1 = PostVisibility.PRIVATE;
	private static final String POST_TITLE_2 = "post2";
	private static final String POST_CONTENT_2 = "post content 2";
	private static final long POST_TIMESTAMP_2 = 100;
	private static final PostVisibility POST_VISIBILITY_2 = PostVisibility.PUBLIC;
	private static final String USER_ID_1 = "userId1";
	private static final String USERNAME_1 = "username1";
	private static final String PASSWORD_1 = "password1";
	private static final String USER_DESCRIPTION_1 = "user-description1";
	private static final String USER_ID_2 = "userId2";
	private static final String USERNAME_2 = "username2";
	private static final String PASSWORD_2 = "password2";
	private static final String USER_DESCRIPTION_2 = "user-description2";
	private JsfConnector connector;
	
	@Before
	public void setUp() {
		connector = new JsfConnector();
	}
	
	@Test
	public void testGetViewPost() {
		com.armstrongmsg.socialnet.model.Post modelPost = 
				new com.armstrongmsg.socialnet.model.Post(POST_TITLE_1, POST_TIMESTAMP_1, POST_CONTENT_1, POST_VISIBILITY_1);
		
		Post viewPost = connector.getViewPost(modelPost);
		assertEquals(POST_TITLE_1, viewPost.getTitle());
		assertEquals(POST_CONTENT_1, viewPost.getContent());
		assertEquals(POST_VISIBILITY_1.getValue(), viewPost.getVisibility());
	}
	
	@Test
	public void testGetViewPosts() {
		com.armstrongmsg.socialnet.model.Post modelPost1 = 
				new com.armstrongmsg.socialnet.model.Post(POST_TITLE_1, POST_TIMESTAMP_1, POST_CONTENT_1, POST_VISIBILITY_1);
		com.armstrongmsg.socialnet.model.Post modelPost2 = 
				new com.armstrongmsg.socialnet.model.Post(POST_TITLE_2, POST_TIMESTAMP_2, POST_CONTENT_2, POST_VISIBILITY_2);
		
		List<Post> viewPosts = connector.getViewPosts(Arrays.asList(modelPost1, modelPost2));
		assertEquals(2, viewPosts.size());
		assertEquals(POST_TITLE_1, viewPosts.get(0).getTitle());
		assertEquals(POST_CONTENT_1, viewPosts.get(0).getContent());
		assertEquals(POST_VISIBILITY_1.getValue(), viewPosts.get(0).getVisibility());
		assertEquals(POST_TITLE_2, viewPosts.get(1).getTitle());
		assertEquals(POST_CONTENT_2, viewPosts.get(1).getContent());
		assertEquals(POST_VISIBILITY_2.getValue(), viewPosts.get(1).getVisibility());
	}
	
	@Test
	public void testGetViewUser() {
		com.armstrongmsg.socialnet.model.User modelUser =
				new com.armstrongmsg.socialnet.model.User(USER_ID_1, USERNAME_1, PASSWORD_1, new Profile(USER_DESCRIPTION_1, null));
		
		User viewUser = connector.getViewUser(modelUser);
		
		assertEquals(USER_ID_1, viewUser.getUserId());
		assertEquals(USERNAME_1, viewUser.getUsername());
		assertEquals(USER_DESCRIPTION_1, viewUser.getProfileDescription());
	}
	
	@Test
	public void testGetViewUsers() {
		com.armstrongmsg.socialnet.model.User modelUser1 =
				new com.armstrongmsg.socialnet.model.User(USER_ID_1, USERNAME_1, PASSWORD_1, new Profile(USER_DESCRIPTION_1, null));
		com.armstrongmsg.socialnet.model.User modelUser2 =
				new com.armstrongmsg.socialnet.model.User(USER_ID_2, USERNAME_2, PASSWORD_2, new Profile(USER_DESCRIPTION_2, null));
		
		List<User> viewUsers = connector.getViewUsers(Arrays.asList(modelUser1, modelUser2));
		
		assertEquals(2, viewUsers.size());
		assertEquals(USER_ID_1, viewUsers.get(0).getUserId());
		assertEquals(USERNAME_1, viewUsers.get(0).getUsername());
		assertEquals(USER_DESCRIPTION_1, viewUsers.get(0).getProfileDescription());
		assertEquals(USER_ID_2, viewUsers.get(1).getUserId());
		assertEquals(USERNAME_2, viewUsers.get(1).getUsername());
		assertEquals(USER_DESCRIPTION_2, viewUsers.get(1).getProfileDescription());
	}
	
	@Test
	public void testGetViewUserSummary() {
		com.armstrongmsg.socialnet.model.UserSummary modelUserSummary = 
				new com.armstrongmsg.socialnet.model.UserSummary(USERNAME_1, USER_DESCRIPTION_1);
		
		UserSummary viewUserSummary = connector.getViewUserSummary(modelUserSummary);
		
		assertEquals(USERNAME_1, viewUserSummary.getUsername());
		assertEquals(USER_DESCRIPTION_1, viewUserSummary.getProfileDescription());
	}
	
	@Test
	public void testGetViewUserSummaries() {
		com.armstrongmsg.socialnet.model.UserSummary modelUserSummary1 = 
				new com.armstrongmsg.socialnet.model.UserSummary(USERNAME_1, USER_DESCRIPTION_1);
		com.armstrongmsg.socialnet.model.UserSummary modelUserSummary2 = 
				new com.armstrongmsg.socialnet.model.UserSummary(USERNAME_2, USER_DESCRIPTION_2);
		
		List<UserSummary> viewUserSummaries = connector.getViewUserSummaries(Arrays.asList(modelUserSummary1, modelUserSummary2));
		
		assertEquals(2, viewUserSummaries.size());
		assertEquals(USERNAME_1, viewUserSummaries.get(0).getUsername());
		assertEquals(USER_DESCRIPTION_1, viewUserSummaries.get(0).getProfileDescription());
		assertEquals(USERNAME_2, viewUserSummaries.get(1).getUsername());
		assertEquals(USER_DESCRIPTION_2, viewUserSummaries.get(1).getProfileDescription());
	}
}
