package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "followBean", eager = true)
@SessionScoped
public class FollowBean {
	private User follower;
	private User followed;
	
	private String username;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	public User getFollower() {
		return follower;
	}
	
	public void setFollower(User follower) {
		this.follower = follower;
	}
	
	public User getFollowed() {
		return followed;
	}
	
	public void setFollowed(User followed) {
		this.followed = followed;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void addFollowAdmin() {
		try {
			facade.addFollowAdmin(SessionManager.getCurrentSession().getUserToken(), 
					SessionManager.getCurrentSession().getUserToken().getUserId(), followed.getUserId());
		} catch (UnauthorizedOperationException | AuthenticationException e) {
			// FIXME treat exception
		}
	}
	
	public void addFollow() {
		try {
			facade.addFollow(SessionManager.getCurrentSession().getUserToken(), getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			//FIXME treat exception
		}
	}
}
