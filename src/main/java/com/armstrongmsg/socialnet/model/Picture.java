package com.armstrongmsg.socialnet.model;

import java.util.Arrays;
import java.util.Objects;

public class Picture extends Media {
	private String id;
	private byte[] data;
	private String path;
	
	public Picture() {
		
	}
	
	public Picture(String id, byte[] data, String pictureLocalPath) {
		this.id = id;
		this.data = data;
		this.path = pictureLocalPath;
	}

	public Picture(String id, byte[] data) {
		this.id = id;
		this.data = data;
		this.path = "";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Objects.hash(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Picture other = (Picture) obj;
		return Arrays.equals(data, other.data) && Objects.equals(id, other.id);
	}
}
