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

import com.armstrongmsg.socialnet.model.Picture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class MediaServicePictureRepository implements PictureRepository {
	private String serviceUrl;
	private CloseableHttpClient httpclient;
	
	public MediaServicePictureRepository(String mediaServiceUrl, String port) {
		serviceUrl = mediaServiceUrl + ":" + port + "/media";
		httpclient = HttpClients.createDefault();
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
			    return new Picture(id, content, this.serviceUrl + "/" + id);
			} finally {
			    response.close();
			}
		} catch (ClientProtocolException e) {
			// TODO handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
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
			// TODO handle exception
			e.printStackTrace();
		} catch (IOException e) {
			// TODO handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void deletePicture(Picture picture) {
		// TODO Auto-generated method stub
	}
}
