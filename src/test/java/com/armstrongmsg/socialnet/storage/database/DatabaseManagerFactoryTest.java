package com.armstrongmsg.socialnet.storage.database;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class DatabaseManagerFactoryTest {
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private DatabaseManagerFactory dbManagerFactory;
	
	@Before
	public void setUp() {
		dbManagerFactory = new DatabaseManagerFactory();
	}
	
	@Test
	public void testLoadCacheFromConfiguration() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME)).
			thenReturn(InMemoryDatabaseManager.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}
	
	@After
	public void tearDown() {
		propertiesUtilMock.close();
	}
}
