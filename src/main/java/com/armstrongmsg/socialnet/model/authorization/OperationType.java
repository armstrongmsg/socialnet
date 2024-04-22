package com.armstrongmsg.socialnet.model.authorization;

public enum OperationType {
	ADD_USER("ADD_USER"), 
	GET_FRIENDS_ADMIN("GET_FRIENDS"), 
	GET_FRIENDS_POSTS("GET_FRIENDS_POSTS"), 
	GET_USER_POSTS("GET_USER_POSTS"), 
	CREATE_POST("CREATE_POST"),
	ADD_FOLLOW("ADD_FOLLOW"), 
	ADD_FRIENDSHIP_ADMIN("ADD_FRIENDSHIP_ADMIN"),
	REMOVE_USER("REMOVE_USER"), 
	GET_FOLLOWED_USERS_ADMIN("GET_FOLLOWED_USERS_ADMIN"), 
	GET_ALL_USERS("GET_ALL_USERS"), 
	GET_SELF_POSTS("GET_SELF_POSTS"), 
	ADD_FRIENDSHIP("ADD_FRIENDSHIP"), 
	GET_FRIENDS("GET_FRIENDS"), 
	ADD_FOLLOW_ADMIN("ADD_FOLLOW_ADMIN"),
	GET_FOLLOWED_USERS("GET_FOLLOWED_USERS"),
	GET_USER_SUMMARIES("GET_USER_SUMMARIES"), 
	GET_USER_RECOMMENDATIONS("GET_USER_RECOMMENDATIONS");

	private String value;

	private OperationType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
