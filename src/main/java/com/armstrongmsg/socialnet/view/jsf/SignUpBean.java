package com.armstrongmsg.socialnet.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.armstrongmsg.socialnet.core.ApplicationFacade;

@ManagedBean(name = "signUpBean", eager = true)
@RequestScoped
public class SignUpBean {
	private String username;
	private String profileDescription;
	private String password;
	
	private static ApplicationFacade facade = ApplicationFacade.getInstance();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProfileDescription() {
		return profileDescription;
	}

	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String signUp() {
		facade.addUser(username, getPassword(), profileDescription);
		return new NavigationController().showHome();
	}
}
