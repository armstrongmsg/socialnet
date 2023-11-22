package com.armstrongmsg.socialnet.model.authorization;

public enum OperationType {
	ADD_USER("ADD_USER"), 
	GET_FRIENDS("GET_FRIENDS"), 
	GET_FRIENDS_POSTS("GET_FRIENDS_POSTS"), 
	GET_USER_POSTS("GET_USER_POSTS"), 
	CREATE_POST("CREATE_POST"),
	ADD_FOLLOW("ADD_FOLLOW"), 
	ADD_FRIENDSHIP("ADD_FRIENDSHIP"),
	REMOVE_USER("REMOVE_USER"), 
	GET_FOLLOWED_USERS("GET_FOLLOWED_USERS"), 
	GET_ALL_USERS("GET_ALL_USERS");

	private String value;

	private OperationType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
