package com.armstrongmsg.socialnet.core.authorization;

public enum OperationType {
	ADD_USER("ADD_USER"), 
	GET_FRIENDS_ADMIN("GET_FRIENDS"), 
	GET_FRIENDS_POSTS("GET_FRIENDS_POSTS"), 
	GET_USER_POSTS_ADMIN("GET_USER_POSTS_ADMIN"), 
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
	GET_USER_RECOMMENDATIONS("GET_USER_RECOMMENDATIONS"), 
	IS_FRIEND("IS_FRIEND"),
	GET_SELF("GET_SELF"), 
	FOLLOWS("FOLLOWS"),
	GET_USER_POSTS("GET_USER_POSTS"), 
	ADD_FRIENDSHIP_REQUEST("ADD_FRIENDSHIP_REQUEST"), 
	GET_SENT_FRIENDSHIP_REQUESTS("GET_SENT_FRIENDSHIP_REQUESTS"), 
	GET_RECEIVED_FRIENDSHIP_REQUESTS("GET_RECEIVED_FRIENDSHIP_REQUESTS"), 
	ACCEPT_FRIENDSHIP_REQUEST("ACCEPT_FRIENDSHIP_REQUEST"), 
	REJECT_FRIENDSHIP_REQUEST("REJECT_FRIENDSHIP_REQUEST"), 
	GET_FEED_POSTS("GET_FEED_POSTS"), 
	UNFOLLOW("UNFOLLOW"),
	UNFRIEND("UNFRIEND"), 
	DELETE_POST("DELETE_POST"), 
	CHANGE_SELF_PROFILE_PIC("CHANGE_SELF_PROFILE_PIC"), 
	GET_USER_PROFILE_PIC("GET_USER_PROFILE_PIC"),
	UPDATE_USER_PROFILE("UPDATE_USER_PROFILE"), 
	GET_FOLLOW_RECOMMENDATIONS("GET_FOLLOW_RECOMMENDATIONS"), 
	GET_MEDIA_URI("GET_MEDIA_URI");

	private String value;

	private OperationType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
