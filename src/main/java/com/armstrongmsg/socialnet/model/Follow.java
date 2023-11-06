package com.armstrongmsg.socialnet.model;

public class Follow extends Relationship {
	private User follower;
	private User followed;
	
	public Follow(User follower, User followed) {
		this.follower = follower;
		this.followed = followed;
	}

	public User getFollower() {
		return follower;
	}

	public User getFollowed() {
		return followed;
	}
}
