package com.armstrongmsg.socialnet.view.jsf.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;

@ManagedBean(name = "editProfileBean", eager = true)
@RequestScoped
public class EditProfileBean {
	private String profileDescription;
	private UploadedFile profilePic;
	private ApplicationFacade facade = ApplicationFacade.getInstance();
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	public String getProfileDescription() {
		return profileDescription;
	}

	public void setProfileDescription(String profileDescription) {
		this.profileDescription = profileDescription;
	}
	
	public UploadedFile getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(UploadedFile profilePic) {
		this.profilePic = profilePic;
	}
	
	public ContextBean getContextBean() {
		return contextBean;
	}
	
	public void setContextBean(ContextBean contextBean) {
		this.contextBean = contextBean;
	}
	
	public void updateProfile() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		UserToken currentUserToken = contextBean.getCurrentSession().getUserToken();
		
		facade.updateProfile(currentUserToken, profileDescription, profilePic.getContent());
	}
}
