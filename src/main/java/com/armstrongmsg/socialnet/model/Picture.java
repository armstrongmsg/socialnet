package com.armstrongmsg.socialnet.model;

public class Picture extends Media {
	private String id;
	private byte[] data;

	public Picture() {
		
	}
	
	public Picture(String id, byte[] data) {
		this.id = id;
		this.data = data;
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
}
