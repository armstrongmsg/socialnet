package com.armstrongmsg.socialnet.view.jsf.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.InvalidParameterException;

@ManagedBean(name = "editProfileBean", eager = true)
@RequestScoped
public class EditProfileBean {
	private String profileDescription;
	private UploadedFile profilePic;
	private ApplicationFacade facade;
	private JsfExceptionHandler exceptionHandler;
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	@ManagedProperty(value="#{applicationBean}")
	private ApplicationBean applicationBean;
	
	@PostConstruct
	public void initialize() {
		facade = applicationBean.getFacade();
		exceptionHandler = new JsfExceptionHandler();
	}
	
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

	public ApplicationBean getApplicationBean() {
		return applicationBean;
	}

	public void setApplicationBean(ApplicationBean applicationBean) {
		this.applicationBean = applicationBean;
	}
	
	public void updateProfile() {
		String currentUserToken = contextBean.getCurrentSession().getUserToken();
		
		try {
			facade.updateProfile(currentUserToken, profileDescription, profilePic.getContent());
		} catch (AuthenticationException | InternalErrorException | InvalidParameterException e) {
			this.exceptionHandler.handle(e);
		}
	}
}
