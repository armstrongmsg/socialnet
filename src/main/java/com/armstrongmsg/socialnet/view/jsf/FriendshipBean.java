package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "friendshipBean", eager = true)
@SessionScoped
public class FriendshipBean {
	private User user1;
	private User user2;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();
	
	public User getUser1() {
		return user1;
	}
	
	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}
	
	public void addFriendship() {
		facade.addFriendship(user1.getUserId(), user2.getUserId());
	}	
}
