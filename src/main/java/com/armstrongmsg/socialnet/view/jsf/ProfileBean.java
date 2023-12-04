package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "profileBean", eager = true)
@SessionScoped
public class ProfileBean {
	private UserSummary summary;

	public String getUsername() {
		return summary.getUsername();
	}
	
	public String getProfileDescription() {
		return summary.getProfileDescription();
	}
	
	public void setUser(UserSummary summary) {
		this.summary = summary;
	}
}
