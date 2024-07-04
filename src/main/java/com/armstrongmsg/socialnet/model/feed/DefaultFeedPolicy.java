package com.armstrongmsg.socialnet.model.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Post;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

// TODO test
public class DefaultFeedPolicy implements FeedPolicy {
	private Integer maxNumberOfPosts;
	
	public DefaultFeedPolicy() throws FatalErrorException {
		String maxNumberOfPostsProperty;
			maxNumberOfPostsProperty = PropertiesUtil.getInstance().getProperty(
					ConfigurationProperties.MAX_NUMBER_OF_POSTS);
			this.maxNumberOfPosts = Integer.valueOf(maxNumberOfPostsProperty);
	}
	
	@Override
	public List<Post> filter(List<Post> posts) {
		NavigableSet<Post> postsWithNoDuplicates = new TreeSet<Post>(posts);
		NavigableSet<Post> postsInReverseOrder = postsWithNoDuplicates.descendingSet();
		return new ArrayList<Post>(postsInReverseOrder).
				subList(0, Math.min(maxNumberOfPosts, posts.size()));
	}
}
