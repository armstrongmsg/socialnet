package com.armstrongmsg.socialnet.view.jsf.model;

import java.util.ArrayList;
import java.util.List;

public class JsfConnector {

	public User getViewUser(com.armstrongmsg.socialnet.model.User modelUser) {
		return new User(modelUser.getUserId(), modelUser.getUsername(), 
				modelUser.getProfile().getDescription());
	}
	
	public List<User> getViewUsers(List<com.armstrongmsg.socialnet.model.User> modelUsers) {
		List<User> newViewUsers = new ArrayList<User>();
		
		for (com.armstrongmsg.socialnet.model.User modelUser : modelUsers) {
			newViewUsers.add(getViewUser(modelUser));
		}
		
		return newViewUsers;
	}
}
