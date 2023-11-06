package com.armstrongmsg.socialnet.model;

import java.util.GregorianCalendar;

public class Post {
	private String title;
	private GregorianCalendar date;
	private String content;
	private PostVisibility visibility;
	
	public Post(String title, GregorianCalendar date, String content, PostVisibility visibility) {
		this.title = title;
		this.date = date;
		this.content = content;
		this.visibility = visibility;
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
}
