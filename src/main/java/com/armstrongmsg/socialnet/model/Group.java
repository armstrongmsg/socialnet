package com.armstrongmsg.socialnet.model;

import java.util.List;

public class Group {
	private String name;
	private List<User> members;
	private List<GroupPost> posts;
	
	public Group(String name, List<User> members, List<GroupPost> posts) {
		this.name = name;
		this.members = members;
		this.posts = posts;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<User> getMembers() {
		return members;
	}
	
	public List<GroupPost> getPosts() {
		return posts;
	}
}
