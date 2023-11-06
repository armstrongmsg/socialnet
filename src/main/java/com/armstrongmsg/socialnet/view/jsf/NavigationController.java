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
		if (pageId == null) {
			return "home";
		}
		
		switch(getPageId()) {
			case "home": return "home";
			case "list-users": return "list-users";
			case "add-user": return "add-user";
			case "manage-users": return "manage-users";
			case "show-user": return "show-user";
		}
		
		return "";
	}
}
