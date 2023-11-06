package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.List;

import com.armstrongmsg.socialnet.model.Admin;
import com.armstrongmsg.socialnet.model.Group;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.model.Relationship;
import com.armstrongmsg.socialnet.model.User;

public class ApplicationFacade {
	private static ApplicationFacade instance;
	
	private Network network;
	
	private ApplicationFacade() {
		Admin admin = new Admin("admin", "admin");
		List<User> users = new ArrayList<User>();
		List<Group> groups = new ArrayList<Group>();
		List<Relationship> relationships = new ArrayList<Relationship>();
		this.network = new Network(admin, users, groups, relationships);
	}
	
	public static ApplicationFacade getInstance() {
		if (instance == null) {
			instance = new ApplicationFacade();
		}
		
		return instance;
	}
	
	public void addUser(String userId, String username, String profileDescription) {
		this.network.addUser(userId, username, profileDescription);
	}
	
	public void removeUser(String userId) {
		this.network.removeUser(userId);
	}
	
	public User getAdmin() { 
		return this.network.getAdmin();
	}
	
	public List<User> getUsers() {
		return this.network.getUsers();
	}
}
