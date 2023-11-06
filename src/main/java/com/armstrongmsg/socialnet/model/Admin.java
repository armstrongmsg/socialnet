package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;

public class Admin extends User {
	public Admin(String username, String userId) {
		super(username, userId, new Profile("", new ArrayList<Post>()));
	}
}
