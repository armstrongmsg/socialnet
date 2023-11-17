package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "followBean", eager = true)
@SessionScoped
public class FollowBean {
	private User follower;
	private User followed;
	
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
	
	public void addFollow() {
		facade.addFollow(SessionManager.getCurrentSession().getUserToken(), 
				SessionManager.getCurrentSession().getUserToken().getUserId(), followed.getUserId());
	}
}
