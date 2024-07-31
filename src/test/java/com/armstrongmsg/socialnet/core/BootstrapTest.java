package com.armstrongmsg.socialnet.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class BootstrapTest {
	private static final String USERNAME_1 = "username1";
	private static final String USERNAME_2 = "username2";
	private static final String USERNAME_3 = "username3";
	private static final String USER_DESCRIPTION_1 = "userdescription1";
	private static final String USER_DESCRIPTION_2 = "userdescription2";
	private static final String USER_DESCRIPTION_3 = "userdescription3";
	private static final String USER_PASSWORD_1 = "password1";
	private static final String USER_PASSWORD_2 = "password2";
	private static final String USER_PASSWORD_3 = "password3";
	private Bootstrap bootstrap;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private Network network;
	private PropertiesUtil propertiesUtil;
	
	@Before
	public void setUp() throws FatalErrorException {
		ApplicationFacade.reset();
				
		this.propertiesUtil = Mockito.mock(PropertiesUtil.class);

		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_USERS)).
			thenReturn(String.join(SystemConstants.BOOTSTRAP_USERS_SEPARATOR, USERNAME_1, USERNAME_2, USERNAME_3));
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_DESCRIPTION + USERNAME_1)).
			thenReturn(USER_DESCRIPTION_1);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_DESCRIPTION + USERNAME_2)).
			thenReturn(USER_DESCRIPTION_2);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_DESCRIPTION + USERNAME_3)).
			thenReturn(USER_DESCRIPTION_3);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_PASSWORD + USERNAME_1)).
			thenReturn(USER_PASSWORD_1);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_PASSWORD + USERNAME_2)).
			thenReturn(USER_PASSWORD_2);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_PASSWORD + USERNAME_3)).
			thenReturn(USER_PASSWORD_3);
		
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		this.network = Mockito.mock(Network.class);
		
		this.bootstrap = new Bootstrap();
	}
	
	@Test
	public void testStartNetwork() throws FatalErrorException {
		this.bootstrap = new Bootstrap();
		this.bootstrap.startNetwork(network);
		
		Mockito.verify(this.network).addUser(USERNAME_1, USER_PASSWORD_1, USER_DESCRIPTION_1);
		Mockito.verify(this.network).addUser(USERNAME_2, USER_PASSWORD_2, USER_DESCRIPTION_2);
		Mockito.verify(this.network).addUser(USERNAME_3, USER_PASSWORD_3, USER_DESCRIPTION_3);
	}
	
	@Test(expected = FatalErrorException.class)
	public void testConstructorChecksNullBootstrapUsers() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_USERS)).
			thenReturn(null);
		new Bootstrap();
	}
	
	@Test(expected = FatalErrorException.class)
	public void testConstructorChecksEmptyBootstrapUsers() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_USERS)).
			thenReturn("");
	
		new Bootstrap();
	}
	
	@Test(expected = FatalErrorException.class)
	public void testConstructorChecksEmptyBootstrapUsername() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_USERS)).
			thenReturn(String.join(SystemConstants.BOOTSTRAP_USERS_SEPARATOR, USERNAME_1, "", USERNAME_3));
	
		new Bootstrap();
	}
	
	@Test(expected = FatalErrorException.class)
	public void testConstructorChecksNullProfileDescription() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_DESCRIPTION + USERNAME_1)).
			thenReturn(null);
		
		new Bootstrap();
	}
	
	@Test(expected = FatalErrorException.class)
	public void testConstructorChecksNullPassword() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.BOOTSTRAP_PASSWORD + USERNAME_1)).
			thenReturn(null);
		
		new Bootstrap();
	}
	
	@After
	public void TearDown() {
		propertiesUtilMock.close();
	}
}
