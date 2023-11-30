package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "profileBean", eager = true)
@SessionScoped
public class ProfileBean {
	private UserSummary summary;

	public UserSummary getSummary() {
		return summary;
	}

	public void setSummary(UserSummary summary) {
		this.summary = summary;
	}
}
