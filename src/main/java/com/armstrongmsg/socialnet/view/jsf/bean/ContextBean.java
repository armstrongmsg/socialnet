package com.armstrongmsg.socialnet.view.jsf.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.exceptions.UserNotFoundException;
import com.armstrongmsg.socialnet.model.FriendshipRequest;
import com.armstrongmsg.socialnet.util.ImageUtils;
import com.armstrongmsg.socialnet.view.jsf.NavigationController;
import com.armstrongmsg.socialnet.view.jsf.Session;
import com.armstrongmsg.socialnet.view.jsf.SessionManager;
import com.armstrongmsg.socialnet.view.jsf.model.JsfConnector;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

@ManagedBean(name = "contextBean", eager = true)
@RequestScoped
public class ContextBean {
	private static final String IMAGE_TYPE = "image/jpeg";
	private static final int PIC_MAX_WIDTH = 300;
	private static final int PIC_MAX_HEIGHT = 300;
	
	private String username;
	private String password;
	private UserSummary loggedUserSummary;
	private boolean loginError;
	private UploadedFile profilePic;
	
	private ApplicationFacade facade = ApplicationFacade.getInstance();
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLoginError() {
		return loginError;
	}

	public void setLoginError(boolean loginError) {
		this.loginError = loginError;
	}

	public UploadedFile getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(UploadedFile profilePic) {
		this.profilePic = profilePic;
	}
	
	public UserSummary getViewUser() {
		UserSummary currentViewUser = SessionManager.getCurrentSession().getCurrentViewUser();
		
		if (currentViewUser == null) {
			try {
				UserToken currentUserToken = SessionManager.getCurrentSession().getUserToken();
				currentViewUser = new JsfConnector().getViewUserSummary(facade.getSelf(currentUserToken));
				SessionManager.getCurrentSession().setCurrentViewUser(currentViewUser);
			} catch (AuthenticationException e) {
				// FIXME treat this exception
				e.printStackTrace();
			} catch (UnauthorizedOperationException e) {
				// FIXME treat this exception
				e.printStackTrace();
			}
		}
		
		return currentViewUser;
	}

	public void setViewUser(UserSummary viewUser) {
		SessionManager.getCurrentSession().setCurrentViewUser(viewUser);
	}
	
	public UserToken getUser() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return new UserToken("no context", "no context", "no context");
		}
		
		return SessionManager.getCurrentSession().getUserToken();
	}

	public String login() {
		try {
			Map<String, String> credentials = new HashMap<String, String>();
			credentials.put(AuthenticationParameters.USERNAME_KEY, username);
			credentials.put(AuthenticationParameters.PASSWORD_KEY, password);
			
			UserToken token = facade.login(credentials);
			boolean userIsAdmin = facade.userIsAdmin(username);
			SessionManager.startSession(token, userIsAdmin);
			
			if (userIsAdmin) {
				return new NavigationController().showAdminHome();
			} else {
				return new NavigationController().showUserHome();
			}
		} catch (AuthenticationException e) {
			setLoginError(true);
			return new NavigationController().showHome();
		} finally {
			username = null;
			password = null;
		}
	}
	
	public String logout() {
		SessionManager.setCurrentSession(null);
		this.loggedUserSummary = null;
		return new NavigationController().showHome();
	}

	public boolean isLogged() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return false;
		}
		
		return SessionManager.getCurrentSession().isLogged();
	}

	public void setLogged(boolean logged) {
		Session session = SessionManager.getCurrentSession();
		
		if (session != null) {
			SessionManager.getCurrentSession().setLogged(logged);
		}
	}

	public boolean isAdmin() {
		Session session = SessionManager.getCurrentSession();
		
		if (session == null) {
			return false;
		}
		
		return SessionManager.getCurrentSession().isAdmin();
	}

	public void setAdmin(boolean isAdmin) {
		Session session = SessionManager.getCurrentSession();
		
		if (session != null) {
			SessionManager.getCurrentSession().setAdmin(isAdmin);			
		}
	}
	
	public boolean getCanAddAsFriend() {
		return !getIsSelfProfile() && !viewUserIsFriend() && !getFriendRequestIsSent();
	}
	
	private boolean viewUserIsFriend() {
		try {
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return facade.isFriend(token, SessionManager.getCurrentSession().getCurrentViewUser()
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
			UserToken token = SessionManager.getCurrentSession().getUserToken();
			return facade.follows(token, SessionManager.getCurrentSession().getCurrentViewUser()
					.getUsername());
		} catch (AuthenticationException | UnauthorizedOperationException e) {
			// FIXME treat this exception
			return false;
		}
	}

	public boolean getIsSelfProfile() {
		try {
			if (loggedUserSummary == null) {
				UserToken loggedUser = SessionManager.getCurrentSession().getUserToken();
				loggedUserSummary = new JsfConnector().getViewUserSummary(facade.getSelf(loggedUser));
			}
			return loggedUserSummary.equals(SessionManager.getCurrentSession().getCurrentViewUser());
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
			List<FriendshipRequest> requests = facade.getSentFriendshipRequests(SessionManager.getCurrentSession().getUserToken());
			String viewedUsername = SessionManager.getCurrentSession().getCurrentViewUser().getUsername(); 
			
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
			UserToken loggedUserToken = SessionManager.getCurrentSession().getUserToken();
			List<UserSummary> followedUsers = new JsfConnector().getViewUserSummaries(
					facade.getFollowedUsers(loggedUserToken));
			return followedUsers.contains(SessionManager.getCurrentSession().getCurrentViewUser());
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
		
		return false;
	}
	
	public boolean getIsFriend() {
		try {
			UserToken loggedUserToken = SessionManager.getCurrentSession().getUserToken();
			List<UserSummary> friends = new JsfConnector().getViewUserSummaries(
					facade.getSelfFriends(loggedUserToken));
			return friends.contains(SessionManager.getCurrentSession().getCurrentViewUser());
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
			UserToken loggedUserToken = SessionManager.getCurrentSession().getUserToken();
			this.facade.changeSelfProfilePic(loggedUserToken, picData);
			SessionManager.getCurrentSession().setCurrentViewUser(null);
		} catch (AuthenticationException e) {
			// FIXME treat this exception
		} catch (UnauthorizedOperationException e) {
			// FIXME treat this exception
		}
	}
	
	public StreamedContent getUserPic() throws IOException {
		UserSummary viewUser = getViewUser();
		UserToken loggedUserToken = SessionManager.getCurrentSession().getUserToken();
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
