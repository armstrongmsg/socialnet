package com.armstrongmsg.socialnet.view.jsf.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.AuthenticationParameters;
import com.armstrongmsg.socialnet.core.ApplicationFacade;
import com.armstrongmsg.socialnet.core.authentication.UserToken;
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

public class ProfileBeanTest {
	private static final String REGULAR_USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String REGULAR_PROFILE_DESCRIPTION = "regular_description";
	private static final String REGULAR_USERNAME_2 = "username2";
	private static final String REGULAR_PROFILE_DESCRIPTION_2 = "regular_description_2";
	private static final String PICTURE_ID = "pictureId";
	private static final byte[] PICTURE_DATA = null;
	private static final String PICTURE_LOCAL_PATH = "";
	private MockedStatic<ApplicationFacade> facadeMock;
	private ApplicationFacade facade;
	private UserToken userToken;
	private Map<String, String> credentials;
	private Picture profilePicture;
	private ProfileBean bean;
	private ContextBean contextBean;
	private ApplicationBean applicationBean;
	
	@Before
	public void setUp() throws AuthenticationException {
		facadeMock = Mockito.mockStatic(ApplicationFacade.class);
		facade = Mockito.mock(ApplicationFacade.class);
		Mockito.when(ApplicationFacade.getInstance()).thenReturn(facade);
		credentials = new HashMap<String, String>();
		userToken = Mockito.mock(UserToken.class);
		profilePicture = new Picture(PICTURE_ID, PICTURE_DATA, PICTURE_LOCAL_PATH);
		applicationBean = new ApplicationBean(facade);
		contextBean = new ContextBean(applicationBean);
	}
	
	@Test
	public void testCannotAddSelfAsFriend() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertFalse(bean.getCanAddAsFriend());
	}
	
	@Test
	public void testCanAddAsFriendNonFriendUser() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		Mockito.when(facade.isFriend(userToken, REGULAR_USERNAME_2)).thenReturn(false);
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertTrue(bean.getCanAddAsFriend());
	}
	
	@Test
	public void testCannotAddAsFriendAFriendUser() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		Mockito.when(facade.isFriend(userToken, REGULAR_USERNAME_2)).thenReturn(true);
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertFalse(bean.getCanAddAsFriend());
	}
	
	@Test
	public void testCannotFollowSelf() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertFalse(bean.getCanFollow());
	}
	
	@Test
	public void testCanFollowNotFollowedUser() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		Mockito.when(facade.follows(userToken, REGULAR_USERNAME_2)).thenReturn(false);
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertTrue(bean.getCanFollow());
	}
	
	@Test
	public void testCannotFollowAlreadyFollowedUser() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		Mockito.when(facade.follows(userToken, REGULAR_USERNAME_2)).thenReturn(true);
		
		contextBean.setUsername(REGULAR_USERNAME);
		contextBean.setPassword(PASSWORD);
		contextBean.login();
		contextBean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null));
		
		bean = new ProfileBean(contextBean, applicationBean);
		
		assertFalse(bean.getCanFollow());
	}
	
	@After
	public void tearDown() {
		facadeMock.close();
	}
}
