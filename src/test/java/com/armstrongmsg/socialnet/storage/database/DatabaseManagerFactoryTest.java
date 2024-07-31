package com.armstrongmsg.socialnet.storage.database;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class DatabaseManagerFactoryTest {
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private DatabaseManagerFactory dbManagerFactory;
	
	@Before
	public void setUp() {
		dbManagerFactory = new DatabaseManagerFactory();
	}
	
	@Test
	public void testLoadDatabaseManagerFromConfiguration() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME)).
			thenReturn(InMemoryDatabaseManager.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		
		System.out.println(returnedInstance.getClass());
		
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}
	

	@Test
	public void testLoadDatabaseManagerFromConfigurationNullProperty() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME)).
			thenReturn(null);
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}
	
	@Test
	public void testLoadDatabaseManagerFromConfigurationEmptyProperty() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME)).
			thenReturn("");
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}
	
	@Test
	public void testErrorOnLoadingConfiguration() throws FatalErrorException {
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenThrow(FatalErrorException.class);
	
		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}
	
	@Test
	public void testErrorOnInstantiation() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME)).
			thenReturn(InMemoryDatabaseManager.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
	
		ClassFactory classFactory = Mockito.mock(ClassFactory.class);
		Mockito.when(classFactory.createInstance(InMemoryDatabaseManager.class.getCanonicalName())).
			thenThrow(FatalErrorException.class);
		
		dbManagerFactory = new DatabaseManagerFactory(classFactory);

		DatabaseManager returnedInstance = dbManagerFactory.loadDatabaseManagerFromConfiguration();
		assertTrue(returnedInstance instanceof InMemoryDatabaseManager);
	}

	
	@After
	public void tearDown() {
		propertiesUtilMock.close();
	}
}
