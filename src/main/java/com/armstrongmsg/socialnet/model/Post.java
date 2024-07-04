package com.armstrongmsg.socialnet.model;

import java.util.GregorianCalendar;
import java.util.UUID;

public class Post implements Comparable<Post> {
	private String id;
	private String title;
	private GregorianCalendar date;
	private String content;
	private PostVisibility visibility;
	
	public Post(String title, GregorianCalendar date, String content, PostVisibility visibility) {
		this.setId(UUID.randomUUID().toString());
		this.title = title;
		this.date = date;
		this.content = content;
		this.visibility = visibility;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PostVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(PostVisibility visibility) {
		this.visibility = visibility;
	}

	@Override
	public int compareTo(Post o) {
		long diff = this.getDate().getTimeInMillis() - o.getDate().getTimeInMillis();
		
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}
}
