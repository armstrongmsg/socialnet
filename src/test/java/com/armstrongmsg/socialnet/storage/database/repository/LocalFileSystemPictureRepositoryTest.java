package com.armstrongmsg.socialnet.storage.database.repository;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Picture;
import com.armstrongmsg.socialnet.util.ApplicationPaths;
import com.armstrongmsg.socialnet.util.PersistenceTest;

public class LocalFileSystemPictureRepositoryTest extends PersistenceTest {
	private static final String TEST_REPOSITORY_DIRECTORY_PATH = TEST_DIRECTORY + File.separator + "repository";
	private static final String TEST_CACHE_PATH = TEST_DIRECTORY + File.separator + "cache";
	private static final String PICTURE_ID_1 = "pictureId1";
	private static final String PICTURE_ID_2 = "pictureId2";
	private static final byte[] PICTURE_DATA_1 = {1, 1, 1, 1, 1};
	private static final byte[] PICTURE_DATA_2 = {2, 2, 2, 2, 2};
	private LocalFileSystemPictureRepository repository;
	private Picture picture;
	private MockedStatic<ApplicationPaths> pathsUtilMock;
	
	@Before
	@Override
	public void setUp() throws FatalErrorException {
		super.setUp();

		picture = new Picture(PICTURE_ID_1, PICTURE_DATA_1);
		
		pathsUtilMock = Mockito.mockStatic(ApplicationPaths.class);
		
		Mockito.when(ApplicationPaths.getApplicationBasePath()).
			thenReturn(TEST_CACHE_PATH);
	}
	
	@Test
	public void testConstructor() throws FatalErrorException {
		new LocalFileSystemPictureRepository();
		
		File repositoryFile = new File(TEST_CACHE_PATH);
		assertTrue(repositoryFile.exists());
	}
	
	@Test
	public void testSavePicture() {
		repository = new LocalFileSystemPictureRepository(TEST_REPOSITORY_DIRECTORY_PATH);
		repository.savePicture(picture);
		
		File pictureFile = new File(TEST_REPOSITORY_DIRECTORY_PATH + File.separator + PICTURE_ID_1); 
		
		assertTrue(pictureFile.exists());
		assertTrue(pictureFile.isFile());
		assertEquals(pictureFile.length(), PICTURE_DATA_1.length);
	}
	
	@Test
	public void testGetPictureById() throws IOException {
		repository = new LocalFileSystemPictureRepository(TEST_REPOSITORY_DIRECTORY_PATH);
		
		File picture2File = new File(TEST_REPOSITORY_DIRECTORY_PATH + File.separator + PICTURE_ID_2);
		picture2File.createNewFile();
		
		FileOutputStream out = new FileOutputStream(picture2File);
		out.write(PICTURE_DATA_2);
		out.close();
		
		Picture returnedPicture = repository.getPictureById(PICTURE_ID_2);
		assertEquals(PICTURE_ID_2, returnedPicture.getId());
		assertArrayEquals(PICTURE_DATA_2, returnedPicture.getData());
	}
	
	@Test
	public void testGetPictureByIdInvalidId() {
		repository = new LocalFileSystemPictureRepository(TEST_REPOSITORY_DIRECTORY_PATH);
		assertNull(repository.getPictureById("invalidId"));
	}

	@After
	@Override
	public void tearDown() throws IOException {
		pathsUtilMock.close();
		super.tearDown();
	}
}
