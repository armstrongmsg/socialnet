package com.armstrongmsg.socialnet.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.armstrongmsg.socialnet.constants.SystemConstants;

@Embeddable
public class Profile {
	@Column
	private String description;
	@OneToMany(orphanRemoval=true, cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	private List<Post> posts;
	@Column(name = "profile_pic_id")
	private String profilePicId;
	@Transient
	private Picture profilePic;
	
	public Profile() {
		
	}
	
	public Profile(String description, List<Post> posts) {
		this.profilePic = new Picture(SystemConstants.DEFAULT_PROFILE_PIC_ID);
		this.profilePicId = SystemConstants.DEFAULT_PROFILE_PIC_ID;
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

	public String getProfilePicId() {
		return profilePicId;
	}

	public void setProfilePicId(String profilePicId) {
		this.profilePicId = profilePicId;
	}

	public Picture getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(Picture profilePic) {
		this.profilePic = profilePic;
	}
	
	public void createPost(String title, String content, PostVisibility newPostVisibility, Picture postPicture) {
		Post newPost = new Post(title, System.currentTimeMillis(), content, newPostVisibility, postPicture);
		posts.add(newPost);
	}
}
