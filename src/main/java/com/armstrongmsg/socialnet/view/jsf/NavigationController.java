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
	
	public String showPageById(String pageId) {
		if (pageId == null) {
			return "home";
		}
		
		switch(pageId) {
			case "home": return "home";
			case "user-profile": return "user-profile";
			case "sign-up": return "sign-up";
			case "user-home": return "user-home";
			case "admin-home": return "admin-home";
			default: return "home";
		}
	}
}
