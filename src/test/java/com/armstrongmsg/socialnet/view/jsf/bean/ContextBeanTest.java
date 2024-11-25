package com.armstrongmsg.socialnet.view.jsf.bean;

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
import com.armstrongmsg.socialnet.exceptions.AuthenticationException;

public class ContextBeanTest {
	private static final String REGULAR_USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String ADMIN_USERNAME = "admin";
	private static final String USER_TOKEN = "token";
	private ContextBean bean;
	private MockedStatic<ApplicationFacade> facadeMock;
	private ApplicationFacade facade;
	private Map<String, String> credentials;
	
	@Before
	public void setUp() throws AuthenticationException {
		facadeMock = Mockito.mockStatic(ApplicationFacade.class);
		facade = Mockito.mock(ApplicationFacade.class);
		Mockito.when(ApplicationFacade.getInstance()).thenReturn(facade);
		credentials = new HashMap<String, String>();
		bean = new ContextBean(new ApplicationBean(facade));
	}
	
	@Test
	public void testLoginAdmin() throws AuthenticationException {
		Mockito.when(facade.userIsAdmin(ADMIN_USERNAME)).thenReturn(true);
		
		credentials.put(AuthenticationParameters.USERNAME_KEY, ADMIN_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(USER_TOKEN);
		
		
		assertNull(bean.getCurrentSession());
		
		bean.setUsername(ADMIN_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		
		assertEquals(USER_TOKEN, bean.getCurrentSession().getUserToken());
		assertTrue(bean.getCurrentSession().isAdmin());
		assertTrue(bean.getCurrentSession().isLogged());
	}
	
	@Test
	public void testLoginRegularUser() throws AuthenticationException {
		Mockito.when(facade.userIsAdmin(REGULAR_USERNAME)).thenReturn(false);
		
		credentials.put(AuthenticationParameters.USERNAME_KEY, REGULAR_USERNAME);
		credentials.put(AuthenticationParameters.PASSWORD_KEY, PASSWORD);
		
		Mockito.when(facade.login(credentials)).thenReturn(USER_TOKEN);
		
		assertNull(bean.getCurrentSession());
		
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		
		assertEquals(USER_TOKEN, bean.getCurrentSession().getUserToken());
		assertFalse(bean.getCurrentSession().isAdmin());
		assertTrue(bean.getCurrentSession().isLogged());
	}
	
	@Test
	public void testLogout() {
		bean.setUsername(REGULAR_USERNAME);
		bean.setPassword(PASSWORD);
		
		bean.login();
		bean.logout();
		
		assertNull(bean.getCurrentSession());
	}
		
	@After
	public void tearDown() {
		facadeMock.close();
	}
}
