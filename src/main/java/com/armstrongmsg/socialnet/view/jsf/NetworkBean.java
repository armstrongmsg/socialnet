package com.armstrongmsg.socialnet.view.jsf;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.Post;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "networkBean", eager = true)
@SessionScoped
public class NetworkBean {
	private String userId;
	private String username;
	private String password;
	private String profileDescription;

	private User user;
	private List<User> users;

	private static ApplicationFacade facade = ApplicationFacade.getInstance();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProfileDescription() {
		return profileDescription;
	}

	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<User> getUsers() {
		if (users == null) {
			users = new JsfConnector().getViewUsers(facade.getUsers());
		}

		return users;
	}

	public String addUser() {
		facade.addUser(userId, username, getProfileDescription());
		users = new JsfConnector().getViewUsers(facade.getUsers());
		return null;
	}
	
	public String signUp() {
		facade.addUser(username, getPassword(), profileDescription);
		users = new JsfConnector().getViewUsers(facade.getUsers());
		return null;
	}

	public String editUser() {
		getUser().setCanEdit(true);
		return null;
	}

	public String removeUser() {
		facade.removeUser(getUser().getUserId());
		users = new JsfConnector().getViewUsers(facade.getUsers());
		return null;
	}

	public String saveUsers() {
		for (User g : getUsers()) {
			g.setCanEdit(false);
		}

		return null;
	}
	
	public List<User> getFriends() {
		return new JsfConnector().getViewUsers(facade.getFriends(user.getUserId()));
	}
	
	public List<User> getFollows() {
		return new JsfConnector().getViewUsers(facade.getFollowedUsers(user.getUserId()));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Post> getFriendsPosts() {
		return new JsfConnector().getViewPosts(facade.getFriendsPosts(getUser().getUserId()));
	}
}
