package com.armstrongmsg.socialnet.storage.media;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.util.PersistenceTest;
import com.armstrongmsg.socialnet.util.TestFileUtils;

public class LocalFileSystemMediaRepositoryTest extends PersistenceTest {
	private static final byte[] MEDIA_DATA = new byte[] {1, 1, 1};
	private static final byte[] UPDATED_MEDIA_DATA = new byte[] {2, 2, 2};
	private static final String MEDIA_ID = "id";
	private static final String REQUESTER_ID = "requester";
	
	private LocalFileSystemMediaRepository repo;
	
	@Before
	public void setUp() throws FatalErrorException {
		super.setUp();
		
		repo = new LocalFileSystemMediaRepository(PersistenceTest.TEST_DIRECTORY);
	}
	
	@Test
	public void testMediaCreationAndRetrieval() throws InternalErrorException, MediaNotFoundException, IOException {
		repo.createMedia(REQUESTER_ID, MEDIA_ID, null, MEDIA_DATA);
		String localPath = PersistenceTest.TEST_DIRECTORY + LocalFileSystemMediaRepository.DEFAULT_MEDIA_LOCAL_PATH + MEDIA_ID;
		TestFileUtils.assertFileHasContent(localPath, MEDIA_DATA);
	}
	
	@Test
	public void testGetMediaUri() throws IOException, MediaNotFoundException {
		String mediaUri = LocalFileSystemMediaRepository.DEFAULT_MEDIA_LOCAL_PATH + MEDIA_ID;
		String localPath = PersistenceTest.TEST_DIRECTORY + mediaUri;
		TestFileUtils.createFileWithContent(localPath, MEDIA_DATA);
		assertEquals(mediaUri, repo.getMediaUri(REQUESTER_ID, MEDIA_ID));
	}
	
	@Test(expected = MediaNotFoundException.class)
	public void testGetNonExistentMedia() throws MediaNotFoundException {
		repo.getMediaUri(REQUESTER_ID, "invalidMediaId");
	}
	
	@Test
	public void testDeleteMedia() throws IOException, MediaNotFoundException {
		String localPath = PersistenceTest.TEST_DIRECTORY + LocalFileSystemMediaRepository.DEFAULT_MEDIA_LOCAL_PATH + MEDIA_ID;
		TestFileUtils.createFileWithContent(localPath, MEDIA_DATA);
		repo.deleteMedia(REQUESTER_ID, MEDIA_ID);
		File f = new File(PersistenceTest.TEST_DIRECTORY + localPath);
		assertFalse(f.exists());
	}
	
	@Test(expected = MediaNotFoundException.class)
	public void testDeleteNonExistentMedia() throws MediaNotFoundException {
		repo.deleteMedia(REQUESTER_ID, "invalidMediaId");
	}
	
	@Test
	public void testUpdateMedia() throws IOException, InternalErrorException, MediaNotFoundException {
		String localPath = PersistenceTest.TEST_DIRECTORY + LocalFileSystemMediaRepository.DEFAULT_MEDIA_LOCAL_PATH + MEDIA_ID;
		TestFileUtils.createFileWithContent(localPath, MEDIA_DATA);
		repo.updateMedia(MEDIA_ID, MEDIA_ID, null, UPDATED_MEDIA_DATA);
		TestFileUtils.assertFileHasContent(localPath, UPDATED_MEDIA_DATA);
	}

	@Test(expected = MediaNotFoundException.class)
	public void testUpdateNonExistentMedia() throws MediaNotFoundException, InternalErrorException {
		repo.updateMedia(REQUESTER_ID, "invalidMediaId", null, UPDATED_MEDIA_DATA);
	}
	
	@After
	public void tearDown() throws IOException {
		super.tearDown();
	}
}
