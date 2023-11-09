package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;

public class Admin extends User {
	public Admin(String username, String userId, String password) {
		super(username, userId, password, new Profile("", new ArrayList<Post>()));
	}
}
