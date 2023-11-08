package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.view.jsf.model.User;

@ManagedBean(name = "contextBean", eager = true)
@SessionScoped
public class ContextBean {
	private User user;

	public User getUser() {
		if (user == null) {
			return new User("no context", "no context", "no context");
		}
		
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
