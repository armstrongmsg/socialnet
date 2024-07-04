package com.armstrongmsg.socialnet.model.feed;

import java.util.List;

import com.armstrongmsg.socialnet.model.Post;

public interface FeedPolicy {
	List<Post> filter(List<Post> posts);
}
