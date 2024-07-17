package com.armstrongmsg.socialnet.model.feed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.model.PostVisibility;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class DefaultFeedPolicyTest {
	private static final Integer MAX_NUMBER_OF_POSTS = 2;
	private static final String MAX_NUMBER_OF_POSTS_STR = String.valueOf(MAX_NUMBER_OF_POSTS);
	private static final String POST_TITLE_1 = "post1";
	private static final String POST_TITLE_2 = "post2";
	private static final String POST_TITLE_3 = "post3";
	private static final long POST_TIMESTAMP_1 = 0;
	private static final long POST_TIMESTAMP_2 = POST_TIMESTAMP_1 + 1;
	private static final long POST_TIMESTAMP_3 = POST_TIMESTAMP_1 + 2;
	private static final String POST_CONTENT_1 = "post1 content";
	private static final String POST_CONTENT_2 = "post2 content";
	private static final String POST_CONTENT_3 = "post 3 content";
	private static final PostVisibility POST_VISIBILITY_1 = PostVisibility.PRIVATE;
	private static final PostVisibility POST_VISIBILITY_2 = PostVisibility.PUBLIC;
	private static final PostVisibility POST_VISIBILITY_3 = PostVisibility.PRIVATE;
	private DefaultFeedPolicy feedPolicy;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private PropertiesUtil propertiesUtil;
	
	@Before
	public void setUp() throws FatalErrorException {
		propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MAX_NUMBER_OF_POSTS)).
			thenReturn(MAX_NUMBER_OF_POSTS_STR);
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil).thenReturn(propertiesUtil);
	}

	@Test
	public void testConstructor() throws FatalErrorException {
		feedPolicy = new DefaultFeedPolicy();
		assertEquals(MAX_NUMBER_OF_POSTS, feedPolicy.getMaxNumberOfPosts());
	}
	
	@Test(expected = FatalErrorException.class)
	public void constructorThrowsExceptionIfMaxNumberOfPostsIsNull() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MAX_NUMBER_OF_POSTS)).
			thenReturn(null);
	
		new DefaultFeedPolicy();
	}
	
	@Test(expected = FatalErrorException.class)
	public void constructorThrowsExceptionIfMaxNumberOfPostsIsEmpty() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MAX_NUMBER_OF_POSTS)).
			thenReturn("");
	
		new DefaultFeedPolicy();
	}
	
	@Test
	public void testFilter() throws FatalErrorException {
		feedPolicy = new DefaultFeedPolicy();
		
		Post post1 = new Post(POST_TITLE_1, POST_TIMESTAMP_1, POST_CONTENT_1, POST_VISIBILITY_1);
		Post post2 = new Post(POST_TITLE_2, POST_TIMESTAMP_2, POST_CONTENT_2, POST_VISIBILITY_2);
		Post post3 = new Post(POST_TITLE_3, POST_TIMESTAMP_3, POST_CONTENT_3, POST_VISIBILITY_3);
		
	 	List<Post> filteredPosts = feedPolicy.filter(Arrays.asList(post1, post2, post3, post3));
	 	assertEquals(2, filteredPosts.size());
	 	assertEquals(post3, filteredPosts.get(0));
	 	assertEquals(post2, filteredPosts.get(1));
	}
	
	@Test
	public void testFilterEmptyList() throws FatalErrorException {
		feedPolicy = new DefaultFeedPolicy();
		
		List<Post> filteredPosts = feedPolicy.filter(Arrays.asList());
		assertTrue(filteredPosts.isEmpty());
	}
	
	@Test
	public void testFilterWithNumberOfPostsLowerThanLimit() throws FatalErrorException {
		feedPolicy = new DefaultFeedPolicy();
		
		Post post1 = new Post(POST_TITLE_1, POST_TIMESTAMP_1, POST_CONTENT_1, POST_VISIBILITY_1);
		
	 	List<Post> filteredPosts = feedPolicy.filter(Arrays.asList(post1));
	 	assertEquals(1, filteredPosts.size());
	 	assertEquals(post1, filteredPosts.get(0));
	}

	@After
	public void tearDown() {
		propertiesUtilMock.close();
	}
}
