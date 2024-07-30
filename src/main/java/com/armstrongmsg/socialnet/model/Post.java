package com.armstrongmsg.socialnet.model;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Posts")
public class Post implements Comparable<Post> {
	@Id
	@Column(name = "id")
	private String id;
	@Column
	private String title;
	@Column
	private long timestamp;
	@Column
	private String content;
	@Column
	@Enumerated(EnumType.STRING)
	private PostVisibility visibility;
	@Column(name = "picture_id")
	private String pictureId;
	@Transient
	private Picture picture;
	
	public Post() {
		
	}
	
	public Post(String title, long timestamp, String content, PostVisibility visibility, Picture postPicture) {
		this.id = UUID.randomUUID().toString();
		this.title = title;
		this.timestamp = timestamp;
		this.content = content;
		this.visibility = visibility;
		
		if (postPicture != null) {
			this.pictureId = postPicture.getId();
			this.picture = postPicture;
		} else {
			this.pictureId = null;
			this.picture = null;
		}
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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

	public String getPictureId() {
		return this.pictureId;
	}
	
	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	public void setPicture(Picture postPicture) {
		this.picture = postPicture;
	}

	public Picture getPicture() {
		return this.picture;
	}

	@Override
	public int compareTo(Post o) {
		long diff = this.getTimestamp() - o.getTimestamp();
		
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Objects.equals(id, other.id);
	}
}
