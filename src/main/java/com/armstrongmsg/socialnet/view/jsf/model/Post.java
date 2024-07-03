package com.armstrongmsg.socialnet.view.jsf.model;

public class Post {
	private String id;
	private String title;
	private String date;
	private String content;
	private String visibility;
	
	public Post(String id, String title, String date, String content, String visibility) {
		this.setId(id);
		this.setTitle(title);
		this.setDate(date);
		this.setContent(content);
		this.setVisibility(visibility);
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	} 
}
