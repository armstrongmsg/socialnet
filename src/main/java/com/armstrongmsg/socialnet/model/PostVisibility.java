package com.armstrongmsg.socialnet.model;

public enum PostVisibility {
	PRIVATE("PRIVATE"),
	PUBLIC("PUBLIC");

	private String value;
	
	private PostVisibility(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
