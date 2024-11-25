package com.armstrongmsg.socialnet.storage;

import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.storage.media.MediaRepository;

public class MediaStorageFacade {
	private MediaRepository mediaRepository;
	
	public void createMedia(String requester, String id, Map<String, String> metadata, byte[] data) 
			throws InternalErrorException, UnauthorizedOperationException { 
		mediaRepository.createMedia(requester, id, metadata, data);
	}
	
	public String getMediaUri(String requester, String id) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		return mediaRepository.getMediaUri(requester, id);
	}
	
	public void deleteMedia(String requester, String id) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		mediaRepository.deleteMedia(requester, id);
	}
	
	public void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		mediaRepository.updateMedia(requester, id, metadata, data);
	}
	
	public List<String> searchMedia(String requester, String metadataKey, String metadataValue) throws InternalErrorException {
		return mediaRepository.searchMedia(requester, metadataKey, metadataValue);
	}
}
