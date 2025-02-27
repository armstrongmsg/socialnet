package com.armstrongmsg.socialnet.view.jsf.bean;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.InvalidParameterException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.UserView;

@ManagedBean(name = "profileBean", eager = true)
@RequestScoped
public class ProfileBean {
	private UserView loggedUserSummary;
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
	
	public ProfileBean() {
		
	}
	
	ProfileBean(ContextBean contextBean, ApplicationBean applicationBean) {
		this.contextBean = contextBean;
		this.applicationBean = applicationBean;
		this.facade = this.applicationBean.getFacade();
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
	
	public boolean getCanAddAsFriend() {
		return !getIsSelfProfile() && !viewUserIsFriend() && !getFriendRequestIsSent();
	}
	
	private boolean viewUserIsFriend() {
		try {
			String token = contextBean.getCurrentSession().getUserToken();
			return facade.isFriend(token, contextBean.getCurrentSession().getCurrentViewUser()
					.getUsername());
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}
	
	public boolean getCanFollow() {
		return !getIsSelfProfile() && !viewUserIsFollowed();
	}

	private boolean viewUserIsFollowed() {
		try {
			String token = contextBean.getCurrentSession().getUserToken();
			return facade.follows(token, contextBean.getCurrentSession().getCurrentViewUser()
					.getUsername());
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}

	public boolean getIsSelfProfile() {
		try {
			if (loggedUserSummary == null) {
				String loggedUser = contextBean.getCurrentSession().getUserToken();
				loggedUserSummary = new JsfConnector(facade, loggedUser).
						getViewUserSummary(facade.getSelf(loggedUser));
			}
			return loggedUserSummary.equals(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}

	public boolean getFriendRequestIsSent() {
		try {
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			List<FriendshipRequest> requests = facade.getSentFriendshipRequests(loggedUserToken);
			String viewedUsername = contextBean.getCurrentSession().getCurrentViewUser().getUsername(); 
			
			for (FriendshipRequest request : requests) {
				if (request.getRequested().getUsername().equals(viewedUsername)) {
					return true;
				}
			}
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}
	
	public boolean getIsFollowed() {
		try {
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			List<UserView> followedUsers = new JsfConnector(facade, loggedUserToken).getViewUserSummaries(
					facade.getFollowedUsers(loggedUserToken));
			return followedUsers.contains(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}
	
	public boolean getIsFriend() {
		try {
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			List<UserView> friends = new JsfConnector(facade, loggedUserToken).getViewUserSummaries(
					facade.getSelfFriends(loggedUserToken));
			return friends.contains(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		}
		
		return false;
	}
	
	public void saveProfilePic() { 
		try {
			byte[] picData = this.profilePic.getContent();
			String loggedUserToken = contextBean.getCurrentSession().getUserToken();
			this.facade.changeSelfProfilePic(loggedUserToken, picData);
			contextBean.getCurrentSession().setCurrentViewUser(null);
		} catch (AuthenticationException e) {
			this.exceptionHandler.handle(e);
		} catch (InternalErrorException e) {
			this.exceptionHandler.handle(e);
		} catch (InvalidParameterException e) {
			this.exceptionHandler.handle(e);
		}
	}
}
