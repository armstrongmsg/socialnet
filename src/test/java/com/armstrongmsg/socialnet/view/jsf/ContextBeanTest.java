package com.armstrongmsg.socialnet.view.jsf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
import com.armstrongmsg.socialnet.view.jsf.bean.ContextBean;
import com.armstrongmsg.socialnet.view.jsf.model.UserSummary;

public class ContextBeanTest {
	private static final String REGULAR_USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ADMIN_USERNAME = "admin";
	private static final String REGULAR_PROFILE_DESCRIPTION = "regular_description";
	private static final String REGULAR_USERNAME_2 = "username2";
	private static final String REGULAR_PROFILE_DESCRIPTION_2 = "regular_description_2";
	private static final String PICTURE_ID = "pictureId";
	private static final byte[] PICTURE_DATA = null;
	private static final String PICTURE_LOCAL_PATH = "";
	private ContextBean bean;
	private MockedStatic<ApplicationFacade> facadeMock;
	private ApplicationFacade facade;
	private UserToken userToken;
	private Map<String, String> credentials;
	private Picture profilePicture;
	
	@Before
	public void setUp() throws AuthenticationException {
		facadeMock = Mockito.mockStatic(ApplicationFacade.class);
		facade = Mockito.mock(ApplicationFacade.class);
		Mockito.when(ApplicationFacade.getInstance()).thenReturn(facade);
		credentials = new HashMap<String, String>();
		userToken = Mockito.mock(UserToken.class);
		profilePicture = new Picture(PICTURE_ID, PICTURE_DATA, PICTURE_LOCAL_PATH);
	}
	
	@Test
	public void testLoginAdmin() throws AuthenticationException {
		Mockito.when(facade.userIsAdmin(ADMIN_USERNAME)).thenReturn(true);
		
		credentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		
		bean = new ContextBean();
		assertNull(bean.getCurrentSession());
		
		bean.setUsername(ADMIN_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		
		assertEquals(userToken, bean.getCurrentSession().getUserToken());
		assertTrue(bean.getCurrentSession().isAdmin());
		assertTrue(bean.getCurrentSession().isLogged());
	}
	
	@Test
	public void testLoginRegularUser() throws AuthenticationException {
		Mockito.when(facade.userIsAdmin(REGULAR_USERNAME)).thenReturn(false);
		
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		
		bean = new ContextBean();
		assertNull(bean.getCurrentSession());
		
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		
		assertEquals(userToken, bean.getCurrentSession().getUserToken());
		assertFalse(bean.getCurrentSession().isAdmin());
		assertTrue(bean.getCurrentSession().isLogged());
	}
	
	@Test
	public void testLogout() {
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		bean.logout();
		
		assertNull(bean.getCurrentSession());
	}
	
	@Test
	public void testCannotAddSelfAsFriend() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, null, null));
		
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
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null, null));
		
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
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null, null));
		
		assertFalse(bean.getCanAddAsFriend());
	}
	
	@Test
	public void testCannotFollowSelf() throws AuthenticationException, UnauthorizedOperationException {
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(userToken);
		Mockito.when(facade.getSelf(userToken)).thenReturn(
				new com.armstrongmsg.socialnet.model.UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, profilePicture));
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME, REGULAR_PROFILE_DESCRIPTION, null, null));
		
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
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null, null));
		
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
		
		bean = new ContextBean();
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		bean.login();
		
		bean.setViewUser(new UserSummary(REGULAR_USERNAME_2, REGULAR_PROFILE_DESCRIPTION_2, null, null));
		
		assertFalse(bean.getCanFollow());
	}
	
	@After
	public void tearDown() {
		facadeMock.close();
	}
}
