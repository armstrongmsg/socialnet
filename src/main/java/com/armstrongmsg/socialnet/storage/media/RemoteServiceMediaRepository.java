package com.armstrongmsg.socialnet.storage.media;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.armstrongmsg.socialnet.util.HttpResponse;
import com.armstrongmsg.socialnet.util.HttpUtils;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class RemoteServiceMediaRepository implements MediaRepository {
	private static Logger logger = LoggerFactory.getLogger(RemoteServiceMediaRepository.class);
	
	static final String CONTENT_TYPE_KEY = "Content-Type";
	static final String CONTENT_TYPE_VALUE = "application/json";
	static final String MEDIA_ID_KEY = "id";
	static final String METADATA_KEY = "metadata";
	static final String DATA_KEY = "data";

	private String serviceUrl;
	private String servicePublicUrl;
	private HttpUtils httpUtils;
	
	public RemoteServiceMediaRepository() throws FatalErrorException {
		String mediaServiceUrl = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_URL);
		String mediaServicePublicUrl = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_PUBLIC_URL);
		String mediaServicePort = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_PORT);
		String mediaServicePublicPort = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.REMOTE_MEDIA_STORAGE_PUBLIC_PORT);
		
		serviceUrl = mediaServiceUrl + ":" + mediaServicePort + "/media";
		servicePublicUrl = mediaServicePublicUrl + ":" + mediaServicePublicPort + "/media";
		
		logger.info(Messages.Logging.MEDIA_SERVICE_URL, serviceUrl);
		logger.info(Messages.Logging.MEDIA_SERVICE_PUBLIC_URL, servicePublicUrl);
		
		this.httpUtils = new HttpUtils();
	}
	
	public RemoteServiceMediaRepository(String mediaServiceUrl, String mediaServicePublicUrl, String port, String mediaServicePublicPort) {
		serviceUrl = mediaServiceUrl + ":" + port + "/media";
		servicePublicUrl = mediaServicePublicUrl + ":" + mediaServicePublicPort + "/media";
		
		logger.info(Messages.Logging.MEDIA_SERVICE_URL, serviceUrl);
		logger.info(Messages.Logging.MEDIA_SERVICE_PUBLIC_URL, servicePublicUrl);
		
		this.httpUtils = new HttpUtils();
	}
	
	RemoteServiceMediaRepository(String serviceUrl, String servicePublicUrl, HttpUtils httpUtils) {
		this.serviceUrl = serviceUrl;
		this.servicePublicUrl = servicePublicUrl;
		this.httpUtils = httpUtils;
	}

	@Override
	public void createMedia(String requester, String id, Map<String, String> metadata, byte[] data)
			throws InternalErrorException, UnauthorizedOperationException {
		try {
			Map<String, Object> body = new HashMap<String, Object>();
			body.put(MEDIA_ID_KEY, id);
			body.put(METADATA_KEY, metadata);
			body.put(DATA_KEY, data);
			
			Map<String, String> header = new HashMap<String, String>();
			header.put(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
			
			HttpResponse response = httpUtils.post(this.serviceUrl, body, header);
			int code = response.getCode();
			
			if (code > 201) {
				throw new InternalErrorException(
						String.format(Messages.Exception.ERROR_WHILE_ACCESSING_SERVICE_TO_CREATE_MEDIA, 
								id, String.valueOf(code)));
			}
		} catch (IOException e) {
			throw new InternalErrorException(
					String.format(Messages.Exception.ERROR_WHILE_ACCESSING_SERVICE_TO_CREATE_MEDIA, 
							id, e.getMessage()));
		}
	}
	
	@Override
	public String getMediaUri(String requester, String id)
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		// TODO must check if the media exists
		return this.servicePublicUrl + "/" + id;
	}

	@Override
	public void deleteMedia(String requester, String id)
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		try {
			Map<String, String> header = new HashMap<String, String>();
			HttpResponse response = httpUtils.delete(this.serviceUrl + "/" + id, header);
			int code = response.getCode();
			
			if (code > 200) {
				if (code == HttpStatus.SC_NOT_FOUND) {
					throw new MediaNotFoundException(
						String.format(Messages.Exception.ERROR_WHILE_ACCESSING_SERVICE_TO_DELETE_MEDIA, 
								id, String.valueOf(code)));
				} else {
					throw new InternalErrorException(
						String.format(Messages.Exception.ERROR_WHILE_ACCESSING_SERVICE_TO_DELETE_MEDIA, 
							id, String.valueOf(code)));
				}
			}
		} catch (IOException e) {
			throw new InternalErrorException(
					String.format(Messages.Exception.ERROR_WHILE_ACCESSING_SERVICE_TO_DELETE_MEDIA, 
							id, String.valueOf(e.getMessage())));
		}
	}

	@Override
	public void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data)
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		// TODO implement
	}

	@Override
	public List<String> searchMedia(String requester, String metadataKey, String metadataValue)
			throws InternalErrorException {
		// TODO implement
		return null;
	}
}
