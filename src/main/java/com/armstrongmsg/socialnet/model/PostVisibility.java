package com.armstrongmsg.socialnet.model;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<PostVisibility> getValues() {
		List<PostVisibility> valuesList = new ArrayList<PostVisibility>();
		
		for (PostVisibility value : PostVisibility.values()) {
			valuesList.add(value);
		}
		
		return valuesList;
	}
}
