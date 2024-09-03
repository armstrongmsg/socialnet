package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

@ManagedBean(name = "navigationController", eager = true)
public class NavigationController {
	@ManagedProperty(value = "#{param.pageId}")
	private String pageId;

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	
	public String showPage() {
		return this.showPageById(this.pageId);
	}
	
	public String showAdminHome() {
		return showPageById("admin-home");
	}

	public String showUserHome() {
		return showPageById("user-home");
	}
	
	public String showHome() {
		return showPageById("home");
	}
	
	public String showSignUp() {
		return showPageById("sign-up");
	}
	
	public String showPageById(String pageId) {
		if (pageId == null) {
			return "home";
		}
		
		switch(pageId) {
			case "home": return "home?faces-redirect=true";
			case "user-profile": return "user-profile?faces-redirect=true";
			case "edit-user-profile": return "edit-user-profile?faces-redirect=true";
			case "sign-up": return "sign-up?faces-redirect=true";
			case "user-home": return "user-home?faces-redirect=true?faces-redirect=true";
			case "admin-home": return "admin-home?faces-redirect=true";
			case "friends-list": return "friends-list?faces-redirect=true";
			case "follows-list": return "follows-list?faces-redirect=true";
			default: return "home?faces-redirect=true";
		}
	}
}
