package com.armstrongmsg.socialnet.model;

public class GroupPost extends Post {
	private User author;
	
	public GroupPost(User author, String title, long timestamp, String content, PostVisibility visibility) {
		super(title, timestamp, content, visibility);
		this.author = author;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
}
