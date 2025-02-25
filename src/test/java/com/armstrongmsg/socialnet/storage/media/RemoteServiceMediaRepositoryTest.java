package com.armstrongmsg.socialnet.storage.media;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.util.HttpResponse;
import com.armstrongmsg.socialnet.util.HttpUtils;

public class RemoteServiceMediaRepositoryTest {
	private static final String SERVICE_PUBLIC_URL = "public_service";
	private static final String SERVICE_URL = "service";
	private static final String REQUESTER = "requester";
	private static final String MEDIA_ID = "mediaId";
	private static final byte[] DATA = new byte[] {1, 2, 3};
	
	private RemoteServiceMediaRepository r;
	private Map<String, String> metadata = new HashMap<String, String>();
	private HttpUtils httpUtils;
	
	@Before
	public void setUp() { 
		httpUtils = Mockito.mock(HttpUtils.class);
		r = new RemoteServiceMediaRepository(SERVICE_URL, SERVICE_PUBLIC_URL, httpUtils);
	}
	
	@Test
	public void testCreateMedia()
			throws InternalErrorException, UnauthorizedOperationException, IOException {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put(RemoteServiceMediaRepository.MEDIA_ID_KEY, MEDIA_ID);
		body.put(RemoteServiceMediaRepository.METADATA_KEY, metadata);
		body.put(RemoteServiceMediaRepository.DATA_KEY, DATA);
		
		Map<String, String> header = new HashMap<String, String>();
		header.put(RemoteServiceMediaRepository.CONTENT_TYPE_KEY, RemoteServiceMediaRepository.CONTENT_TYPE_VALUE);
		
		HttpResponse response = new HttpResponse(HttpStatus.SC_CREATED, new byte[] {});
		Mockito.when(httpUtils.post(SERVICE_URL, body, header)).thenReturn(response);
		
		r.createMedia(REQUESTER, MEDIA_ID, metadata, DATA);
		
		Mockito.verify(httpUtils).post(SERVICE_URL, body, header);
	}
	
	@Test(expected = InternalErrorException.class)
	public void testCreateMediaThrowsInternalErrorExceptionWhenPostFails() 
			throws InternalErrorException, UnauthorizedOperationException, IOException {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put(RemoteServiceMediaRepository.MEDIA_ID_KEY, MEDIA_ID);
		body.put(RemoteServiceMediaRepository.METADATA_KEY, metadata);
		body.put(RemoteServiceMediaRepository.DATA_KEY, DATA);
		
		Map<String, String> header = new HashMap<String, String>();
		header.put(RemoteServiceMediaRepository.CONTENT_TYPE_KEY, RemoteServiceMediaRepository.CONTENT_TYPE_VALUE);
		
		Mockito.doThrow(IOException.class).when(httpUtils).post(SERVICE_URL, body, header);
		
		r.createMedia(REQUESTER, MEDIA_ID, metadata, DATA);
	}
	
	@Test(expected = InternalErrorException.class)
	public void testCreateMediaThrowsInternalErrorExceptionWhenReceivesErrorCode() 
			throws InternalErrorException, UnauthorizedOperationException, IOException {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put(RemoteServiceMediaRepository.MEDIA_ID_KEY, MEDIA_ID);
		body.put(RemoteServiceMediaRepository.METADATA_KEY, metadata);
		body.put(RemoteServiceMediaRepository.DATA_KEY, DATA);
		
		Map<String, String> header = new HashMap<String, String>();
		header.put(RemoteServiceMediaRepository.CONTENT_TYPE_KEY, RemoteServiceMediaRepository.CONTENT_TYPE_VALUE);
		
		HttpResponse response = new HttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, new byte[] {});
		Mockito.when(httpUtils.post(SERVICE_URL, body, header)).thenReturn(response);
		
		r.createMedia(REQUESTER, MEDIA_ID, metadata, DATA);
	}
	
	@Test
	public void testDeleteMedia() 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException, IOException {
		HttpResponse response = new HttpResponse(HttpStatus.SC_OK, new byte[] {});
		Map<String, String> header = new HashMap<String, String>();
		Mockito.when(httpUtils.delete(SERVICE_URL + "/" + MEDIA_ID, header)).thenReturn(response);
		
		r.deleteMedia(REQUESTER, MEDIA_ID);
		
		Mockito.verify(httpUtils).delete(SERVICE_URL + "/" + MEDIA_ID, header);
	}
	
	@Test(expected = InternalErrorException.class)
	public void testDeleteMediaThrowsInternalErrorExceptionWhenDeleteFails() 
			throws IOException, MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		Map<String, String> header = new HashMap<String, String>();
		Mockito.doThrow(IOException.class).when(httpUtils).delete(SERVICE_URL + "/" + MEDIA_ID, header);
		
		r.deleteMedia(REQUESTER, MEDIA_ID);
	}
	
	@Test(expected = MediaNotFoundException.class)
	public void testDeleteMediaThrowsMediaNotFoundIfReceivesCode404() 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException, IOException {
		HttpResponse response = new HttpResponse(HttpStatus.SC_NOT_FOUND, new byte[] {});
		Map<String, String> header = new HashMap<String, String>();
		Mockito.when(httpUtils.delete(SERVICE_URL + "/" + MEDIA_ID, header)).thenReturn(response);
		
		r.deleteMedia(REQUESTER, MEDIA_ID);
	}
	
	@Test(expected = InternalErrorException.class)
	public void testDeleteMediaThrowsMediaNotFoundIfReceivesError404() 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException, IOException {
		HttpResponse response = new HttpResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, new byte[] {});
		Map<String, String> header = new HashMap<String, String>();
		Mockito.when(httpUtils.delete(SERVICE_URL + "/" + MEDIA_ID, header)).thenReturn(response);
		
		r.deleteMedia(REQUESTER, MEDIA_ID);
	}
}
