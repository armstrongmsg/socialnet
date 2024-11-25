package com.armstrongmsg.socialnet.storage.media;

import java.util.List;
import java.util.Map;

import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;

public interface MediaRepository {
	void createMedia(String requester, String id, Map<String, String> metadata, byte[] data) 
			throws InternalErrorException, UnauthorizedOperationException;
	String getMediaUri(String requester, String id) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException;
	void deleteMedia(String requester, String id) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException;
	void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data) 
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException;
	List<String> searchMedia(String requester, String metadataKey, String metadataValue) 
			throws InternalErrorException;
}
