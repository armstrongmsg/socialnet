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
	
	public PostVisibility fromValue(String value) {
		for (PostVisibility instance : PostVisibility.values()) {
			if (instance.getValue().equals(value)) {
				return instance;
			}
		}
		// FIXME treat this case
		return null;
	}
}
