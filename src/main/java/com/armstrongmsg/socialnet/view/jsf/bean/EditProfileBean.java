package com.armstrongmsg.socialnet.view.jsf.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.view.jsf.SessionManager;

@ManagedBean(name = "editProfileBean", eager = true)
@RequestScoped
public class EditProfileBean {
	private String profileDescription;
	private UploadedFile profilePic;
	
	private ApplicationFacade facade = ApplicationFacade.getInstance();
	
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
	
	public void updateProfile() throws AuthenticationException, UnauthorizedOperationException, UserNotFoundException {
		UserToken currentUserToken = SessionManager.getCurrentSession().getUserToken();
		
		facade.updateProfile(currentUserToken, profileDescription, profilePic.getContent());
	}
}
