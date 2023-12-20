package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.armstrongmsg.socialnet.model.authentication.UserToken;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

// TODO should be request scoped
@ManagedBean(name = "profileBean", eager = true)
@SessionScoped
public class ProfileBean {
	private UserSummary summary;

	public String getUsername() {
		if (summary == null) {
			// FIXME should not depend on token structure
			UserToken currentUserToken = SessionManager.getCurrentSession().getUserToken();
			this.summary = new UserSummary(currentUserToken.getUsername(),
					currentUserToken.getProfileDescription());
		}
		
		return summary.getUsername();
	}
	
	public String getProfileDescription() {
		return summary.getProfileDescription();
	}
	
	public void setUser(UserSummary summary) {
		this.summary = summary;
	}
}
