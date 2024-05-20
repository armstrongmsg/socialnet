package com.armstrongmsg.socialnet.storage.cache;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
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
			thenReturn(DefaultCache.class.getCanonicalName());
	
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		Cache returnedInstance = cacheFactory.loadCacheFromConfiguration();
		
		assertTrue(returnedInstance instanceof DefaultCache);
	}
	
	@After
	public void TearDown() {
		propertiesUtilMock.close();
	}
}
