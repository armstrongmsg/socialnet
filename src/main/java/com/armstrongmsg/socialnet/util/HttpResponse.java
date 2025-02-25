package com.armstrongmsg.socialnet.util;

public class HttpResponse {
	private int code;
	private byte[] data;
	
	public HttpResponse(int code, byte[] data) {
		this.setCode(code);
		this.setData(data);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
