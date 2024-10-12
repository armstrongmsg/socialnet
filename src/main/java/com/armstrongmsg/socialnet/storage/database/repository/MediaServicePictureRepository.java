package com.armstrongmsg.socialnet.storage.database.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.model.Picture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

// TODO test
public class MediaServicePictureRepository implements PictureRepository {
	private static Logger logger = LoggerFactory.getLogger(MediaServicePictureRepository.class);
	
	private String serviceUrl;
	private String servicePublicUrl;
	private CloseableHttpClient httpclient;
	
	public MediaServicePictureRepository(String mediaServiceUrl, String mediaServicePublicUrl, String port) {
		serviceUrl = mediaServiceUrl + ":" + port + "/media";
		servicePublicUrl = mediaServicePublicUrl + ":" + port + "/media";
		httpclient = HttpClients.createDefault();
		
		logger.info("Media Service URL: {}", serviceUrl);
		logger.info("Media Service Public URL: {}", servicePublicUrl);
	}
	
	@Override
	public Picture getPictureById(String id) {
		try {
			HttpGet httpGet = new HttpGet(this.serviceUrl + "/" + id);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			
			try {
			    HttpEntity entity = response.getEntity();
			    byte[] content = entity.getContent().readAllBytes();
			    EntityUtils.consume(entity);
			    return new Picture(id, content, this.servicePublicUrl + "/" + id);
			} finally {
			    response.close();
			}
		} catch (ClientProtocolException e) {
			logger.debug("Exception when loading picture:{}, message:{}", 
					id, e.getMessage());
		} catch (IOException e) {
			logger.debug("Exception when loading picture:{}, message:{}", 
					id, e.getMessage());
		}
		return null;
	}

	@Override
	public void savePicture(Picture picture) {
		try {
			HttpPost httpPost = new HttpPost(this.serviceUrl);
			Map<String, Object> body = new HashMap<String, Object>();
			// FIXME constant
			body.put("id", picture.getId());
			body.put("metadata", new HashMap<String, String>());
			body.put("data", picture.getData());
			
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
					picture.getId(), e.getMessage());
		} catch (IOException e) {
			logger.debug("Exception when saving picture:{}, message:{}", 
					picture.getId(), e.getMessage());
		}
	}

	@Override
	public void deletePicture(Picture picture) {
		// TODO Auto-generated method stub
	}
}
