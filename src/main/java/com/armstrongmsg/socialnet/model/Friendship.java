package com.armstrongmsg.socialnet.model;

public class Friendship extends Relationship {
	private User friend1;
	private User friend2;
	
	public Friendship(User friend1, User friend2) {
		this.friend1 = friend1;
		this.friend2 = friend2;
	}

	public User getFriend1() {
		return friend1;
	}

	public User getFriend2() {
		return friend2;
	}
}
