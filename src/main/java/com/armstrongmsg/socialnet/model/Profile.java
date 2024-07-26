package com.armstrongmsg.socialnet.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Embeddable
public class Profile {
	@Column
	private String description;
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	private List<Post> posts;
	
	public Profile() {
		
	}
	
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

	public void createPost(String title, String content, PostVisibility newPostVisibility) {
		Post newPost = new Post(title, System.currentTimeMillis(), content, newPostVisibility);
		posts.add(newPost);
	}

	public byte[] getProfilePic() {
		// TODO Auto-generated method stub
		return null;
	}
}
