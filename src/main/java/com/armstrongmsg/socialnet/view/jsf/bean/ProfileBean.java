package com.armstrongmsg.socialnet.view.jsf.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.util.ImageUtils;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

// TODO test the effect of the 'eager' param
@ManagedBean(name = "profileBean", eager = true)
@RequestScoped
public class ProfileBean {
	private static final String IMAGE_TYPE = "image/jpeg";
	private static final int PIC_MAX_WIDTH = 300;
	private static final int PIC_MAX_HEIGHT = 300;
	
	@ManagedProperty(value="#{contextBean}")
	private ContextBean contextBean;
	
	private ApplicationFacade facade = ApplicationFacade.getInstance();
	private UserSummary loggedUserSummary;
	
	private UploadedFile profilePic;
	
	public ProfileBean() {
		
	}
	
	ProfileBean(ContextBean contextBean) {
		this.contextBean = contextBean;
	}
	
	public ContextBean getContextBean() {
		return contextBean;
	}

	public void setContextBean(ContextBean contextBean) {
		this.contextBean = contextBean;
	}
	
	public boolean getCanAddAsFriend() {
		return !getIsSelfProfile() && !viewUserIsFriend() && !getFriendRequestIsSent();
	}
	
	private boolean viewUserIsFriend() {
		try {
			UserToken token = contextBean.getCurrentSession().getUserToken();
			return facade.isFriend(token, contextBean.getCurrentSession().getCurrentViewUser()
					.getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}
	
	public boolean getCanFollow() {
		return !getIsSelfProfile() && !viewUserIsFollowed();
	}

	private boolean viewUserIsFollowed() {
		try {
			UserToken token = contextBean.getCurrentSession().getUserToken();
			return facade.follows(token, contextBean.getCurrentSession().getCurrentViewUser()
					.getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}

	public boolean getIsSelfProfile() {
		try {
			if (loggedUserSummary == null) {
				UserToken loggedUser = contextBean.getCurrentSession().getUserToken();
				loggedUserSummary = new JsfConnector().getViewUserSummary(facade.getSelf(loggedUser));
			}
			return loggedUserSummary.equals(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			// FIXME treat this exception
			return false;
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}

	public boolean getFriendRequestIsSent() {
		try {
			List<FriendshipRequest> requests = facade.getSentFriendshipRequests(contextBean.getCurrentSession().getUserToken());
			String viewedUsername = contextBean.getCurrentSession().getCurrentViewUser().getUsername(); 
			
			for (FriendshipRequest request : requests) {
				if (request.getRequested().getUsername().equals(viewedUsername)) {
					return true;
				}
			}
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return false;
	}
	
	public boolean getIsFollowed() {
		try {
			UserToken loggedUserToken = contextBean.getCurrentSession().getUserToken();
			List<UserSummary> followedUsers = new JsfConnector().getViewUserSummaries(
					facade.getFollowedUsers(loggedUserToken));
			return followedUsers.contains(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return false;
	}
	
	public boolean getIsFriend() {
		try {
			UserToken loggedUserToken = contextBean.getCurrentSession().getUserToken();
			List<UserSummary> friends = new JsfConnector().getViewUserSummaries(
					facade.getSelfFriends(loggedUserToken));
			return friends.contains(contextBean.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return false;
	}
	
	public void saveProfilePic() throws UserNotFoundException { 
		try {
			byte[] picData = this.profilePic.getContent();
			UserToken loggedUserToken = contextBean.getCurrentSession().getUserToken();
			this.facade.changeSelfProfilePic(loggedUserToken, picData);
			contextBean.getCurrentSession().setCurrentViewUser(null);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
	}
	
	public StreamedContent getUserPic() throws IOException {
		UserSummary viewUser = this.contextBean.getViewUser();
		UserToken loggedUserToken = contextBean.getCurrentSession().getUserToken();
		try {
			byte[] picData = this.facade.getUserPic(loggedUserToken, viewUser.getUsername());
			
			if (picData == null) {
				String defaultProfilePicPath = Thread.currentThread().getContextClassLoader().
						getResource("").getPath() + File.separator + SystemConstants.DEFAULT_PROFILE_PIC;
				InputStream profilePicStream = new FileInputStream(new File(defaultProfilePicPath));
				return DefaultStreamedContent.
						builder().
						contentType(IMAGE_TYPE).
						stream(() -> profilePicStream).
						build();
			} else {
				byte[] rescaledPic = new ImageUtils().rescale(picData, PIC_MAX_HEIGHT, PIC_MAX_WIDTH);
				InputStream profilePicStream = new ByteArrayInputStream(rescaledPic);
				return DefaultStreamedContent.
						builder().
						contentType(IMAGE_TYPE).
						stream(() -> profilePicStream).
						build();
			}
			
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		} catch (UserNotFoundException e) {
			// FIXME treat this exception
		}
		return null;
	}
}