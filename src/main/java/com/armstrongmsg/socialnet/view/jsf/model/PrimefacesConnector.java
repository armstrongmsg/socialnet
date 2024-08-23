package com.armstrongmsg.socialnet.view.jsf.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.armstrongmsg.socialnet.model.Picture;

public class PrimefacesConnector {
	public StreamedContent getOrDefaultStreamContent(Picture picture) {
		StreamedContent content = null;
		
		if (picture != null && picture.getData() != null) {
			byte[] picData = picture.getData();
			content = getContentFromData(picData);
		} else {
			content = getContentFromData(new byte[] {});
		}
		
		return content;
	}
	
	private StreamedContent getContentFromData(byte[] data) {
		InputStream profilePicStream = new ByteArrayInputStream(data);
		return DefaultStreamedContent.
				builder().
				contentType("image/jpeg").
				stream(() -> profilePicStream).
				build();
	}
	
	public String convertPicturePathToWebFormat(String path) {
		// FIXME constant
		if (path != null && !path.isEmpty()) {
			return path.split("socialnet")[1];
		}
		
		return null;
	}
}
