package com.armstrongmsg.socialnet.storage.media;

import static org.junit.Assert.assertEquals;
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

public class MediaRepositoryFactoryTest {
	private static final String MEDIA_REPOSITORY_CLASS_NAME = "mediarepoclassname";
	private MediaRepositoryFactory factory;
	private PropertiesUtil propertiesUtil;
	private ClassFactory classFactory;
	private MockedStatic<PropertiesUtil> propertiesUtilMock;
	private MediaRepository mediaRepository;
	
	@Before
	public void setUp() throws FatalErrorException {
		propertiesUtil = Mockito.mock(PropertiesUtil.class);
		propertiesUtilMock = Mockito.mockStatic(PropertiesUtil.class);
		Mockito.when(PropertiesUtil.getInstance()).thenReturn(propertiesUtil);
		
		mediaRepository = Mockito.mock(MediaRepository.class);
		
		classFactory = Mockito.mock(ClassFactory.class);
		Mockito.when(classFactory.createInstance(MEDIA_REPOSITORY_CLASS_NAME)).thenReturn(mediaRepository);
		
		factory = new MediaRepositoryFactory(classFactory);
	}
	
	@Test
	public void testLoadMediaRepositoryFromConfiguration() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MEDIA_REPOSITORY_CLASS_NAME)).
			thenReturn(MEDIA_REPOSITORY_CLASS_NAME);
		
		MediaRepository returnedRepository = factory.loadMediaRepositoryFromConfiguration();
		
		assertEquals(mediaRepository, returnedRepository);
	}
	
	@Test
	public void testLoadMediaRepositoryFromConfigurationEmptyClassName() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MEDIA_REPOSITORY_CLASS_NAME)).
			thenReturn("");
		
		MediaRepository returnedRepository = factory.loadMediaRepositoryFromConfiguration();
		
		assertTrue(returnedRepository instanceof LocalFileSystemMediaRepository);
	}
	
	@Test
	public void testLoadMediaRepositoryFromConfigurationNullClassName() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MEDIA_REPOSITORY_CLASS_NAME)).
			thenReturn(null);
	
		MediaRepository returnedRepository = factory.loadMediaRepositoryFromConfiguration();
	
		assertTrue(returnedRepository instanceof LocalFileSystemMediaRepository);
	}
	
	@Test
	public void testLoadMediaRepositoryFromConfigurationErrorOnLoadingClassName() throws FatalErrorException { 
		Mockito.when(PropertiesUtil.getInstance()).thenThrow(FatalErrorException.class);
		
		MediaRepository returnedRepository = factory.loadMediaRepositoryFromConfiguration();
		
		assertTrue(returnedRepository instanceof LocalFileSystemMediaRepository);
	}
	
	@Test
	public void testLoadMediaRepositoryFromConfigurationErrorOnCreatingInstance() throws FatalErrorException {
		Mockito.when(propertiesUtil.getProperty(ConfigurationProperties.MEDIA_REPOSITORY_CLASS_NAME)).
			thenReturn(MEDIA_REPOSITORY_CLASS_NAME);
		Mockito.when(classFactory.createInstance(MEDIA_REPOSITORY_CLASS_NAME)).thenThrow(FatalErrorException.class);
		
		MediaRepository returnedRepository = factory.loadMediaRepositoryFromConfiguration();
	
		assertTrue(returnedRepository instanceof LocalFileSystemMediaRepository);
	}
	
	@After
	public void tearDown() {
		propertiesUtilMock.close();
	}
}
