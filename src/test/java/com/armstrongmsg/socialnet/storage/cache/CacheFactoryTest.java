package com.armstrongmsg.socialnet.storage.cache;

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

public class CacheFactoryTest {
	private CacheFactory cacheFactory;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	
	@Before
	public void setUp() {
		cacheFactory = new CacheFactory();
	}
	
	@Test
	public void testLoadCacheFromConfiguration() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty("CACHE_CLASS_NAME")).
			thenReturn(NoOperationCache.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		
		assertTrue(returnedInstance instanceof NoOperationCache);
	}
	
	@Test
	public void testLoadCacheFromConfigurationNullProperty() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.CACHE_CLASS_NAME)).
			thenReturn(null);
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		
		assertTrue(returnedInstance instanceof DefaultCache);
	}
	
	@Test
	public void testLoadCacheFromConfigurationEmptyProperty() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.CACHE_CLASS_NAME)).
			thenReturn("");
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		
		assertTrue(returnedInstance instanceof DefaultCache);
	}
	
	@Test
	public void testErrorOnLoadingConfiguration() throws FatalErrorException {
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenThrow(FatalErrorException.class);
	
		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		assertTrue(returnedInstance instanceof DefaultCache);
	}
	
	@Test
	public void testErrorOnInstantiation() throws FatalErrorException {
		PropertiesUtil propertiesUtil = Mockito.mock(PropertiesUtil.class);
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.CACHE_CLASS_NAME)).
			thenReturn(NoOperationCache.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
	
		ClassFactory classFactory = Mockito.mock(ClassFactory.class);
		Mockito.when(classFactory.createInstance(NoOperationCache.class.getCanonicalName())).
			thenThrow(FatalErrorException.class);
		
		cacheFactory = new CacheFactory(classFactory);

		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		assertTrue(returnedInstance instanceof DefaultCache);
	}
	
	@After
	public void tearDown() {
		propertiesUtilMock.close();
	}
}
