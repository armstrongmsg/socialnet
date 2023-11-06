package com.armstrongmsg.socialnet.model;

import java.util.GregorianCalendar;
import java.util.List;

public class Profile {
	private String description;
	private List<Post> posts;
	
	public Profile(String description, List<Post> posts) {
		this.description = description;
		this.posts = posts;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void createPost(String title, String content, String postVisibility) {
		GregorianCalendar postCreationTime = new GregorianCalendar();
		postCreationTime.setTimeInMillis(System.currentTimeMillis());
		
		Post newPost = new Post(title, postCreationTime, content, PostVisibility.valueOf(postVisibility));
		posts.add(newPost);
	}
}
