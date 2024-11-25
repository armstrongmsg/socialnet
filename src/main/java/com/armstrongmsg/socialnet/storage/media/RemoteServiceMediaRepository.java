package com.armstrongmsg.socialnet.storage.media;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.MediaNotFoundException;
import com.armstrongmsg.socialnet.exceptions.UnauthorizedOperationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class RemoteServiceMediaRepository implements MediaRepository {
	private static Logger logger = LoggerFactory.getLogger(RemoteServiceMediaRepository.class);

	private String serviceUrl;
	private String servicePublicUrl;
	private CloseableHttpClient httpclient;
	
	public RemoteServiceMediaRepository(String mediaServiceUrl, String mediaServicePublicUrl, String port, String mediaServicePublicPort) {
		serviceUrl = mediaServiceUrl + ":" + port + "/media";
		servicePublicUrl = mediaServicePublicUrl + ":" + mediaServicePublicPort + "/media";
		httpclient = HttpClients.createDefault();
		
		logger.info("Media Service URL: {}", serviceUrl);
		logger.info("Media Service Public URL: {}", servicePublicUrl);
	}
	
	@Override
	public void createMedia(String requester, String id, Map<String, String> metadata, byte[] data)
			throws InternalErrorException, UnauthorizedOperationException {
		try {
			HttpPost httpPost = new HttpPost(this.serviceUrl);
			Map<String, Object> body = new HashMap<String, Object>();
			// FIXME constant
			body.put("id", id);
			body.put("metadata", metadata);
			body.put("data", data);
			
		    ObjectWriter ow = new ObjectMapper().writer();
		    String jsonBody = ow.writeValueAsString(body);
			
		    HttpEntity stringEntity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
		    httpPost.setEntity(stringEntity);
			
		    // FIXME constant
			Header header = new BasicHeader("Content-Type", "application/json");
			httpPost.setHeader(header);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			
			try {
				HttpEntity entity = response.getEntity();
				EntityUtils.consume(entity);
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			logger.debug("Exception when saving picture:{}, message:{}", 
					id, e.getMessage());
			// TODO add message
			throw new InternalErrorException();
		} catch (IOException e) {
			logger.debug("Exception when saving picture:{}, message:{}", 
					id, e.getMessage());
			// TODO add message
			throw new InternalErrorException();
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
		// TODO Auto-generated method stub
	}

	@Override
	public void updateMedia(String requester, String id, Map<String, String> metadata, byte[] data)
			throws MediaNotFoundException, InternalErrorException, UnauthorizedOperationException {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> searchMedia(String requester, String metadataKey, String metadataValue)
			throws InternalErrorException {
		// TODO Auto-generated method stub
		return null;
	}
}
