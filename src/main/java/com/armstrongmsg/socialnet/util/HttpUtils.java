package com.armstrongmsg.socialnet.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HttpUtils {
	private CloseableHttpClient httpClient;
	
	public HttpUtils() {
		httpClient = HttpClients.createDefault();
	}
	
	public HttpResponse post(String url, Map<String, Object> body, Map<String, String> header) throws IOException {
		HttpPost httpPost = new HttpPost(url);
	    HttpEntity stringEntity = getEntityFromBody(body);
	    httpPost.setEntity(stringEntity);
		
	    for (String key : header.keySet()) {
	    	BasicHeader basicHeader = new BasicHeader(key, header.get(key));
	    	httpPost.setHeader(basicHeader);
	    }
		
		CloseableHttpResponse response = httpClient.execute(httpPost);
		HttpResponse simplifiedResponse = getResponse(response);
		closeResponse(response);
		return simplifiedResponse;
	}
	
	public HttpResponse delete(String url, Map<String, String> header) throws IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		
		for (String key : header.keySet()) {
	    	BasicHeader basicHeader = new BasicHeader(key, header.get(key));
	    	httpDelete.setHeader(basicHeader);
	    }
		
		CloseableHttpResponse response = httpClient.execute(httpDelete);
		HttpResponse simplifiedResponse = getResponse(response);
		closeResponse(response);
		return simplifiedResponse;
	}

	private void closeResponse(CloseableHttpResponse response) throws IOException {
		try {
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
	}
	
	private HttpResponse getResponse(CloseableHttpResponse r) throws IOException {
		int responseCode = r.getStatusLine().getStatusCode();
		
		BufferedInputStream s = new BufferedInputStream(r.getEntity().getContent());
		byte[] data = s.readAllBytes();
		
		return new HttpResponse(responseCode, data);
	}
	
	private HttpEntity getEntityFromBody(Map<String, Object> body) throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer();
	    String jsonBody = ow.writeValueAsString(body);
		
	    return new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
	}
}
