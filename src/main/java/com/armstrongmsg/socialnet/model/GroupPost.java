package com.armstrongmsg.socialnet.model;

import java.util.GregorianCalendar;

public class GroupPost extends Post {
	private User author;
	
	public GroupPost(User author, String title, GregorianCalendar date, String content, PostVisibility visibility) {
		super(title, date, content, visibility);
		this.author = author;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
}
