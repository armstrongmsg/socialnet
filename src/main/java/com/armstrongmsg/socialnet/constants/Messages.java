package com.armstrongmsg.socialnet.constants;

public class Messages {
	public static class Logging {
		public static final String AUTHENTICATION_EXCEPTION = "Authentication exception. Message:{}.";
		public static final String AUTHORIZATION_EXCEPTION = "Authorization exception. Message:{}.";
		public static final String COULD_NOT_LOAD_ADMIN_CONFIGURATION = "Could not load admin configuration. Message: {}";
		public static final String ERROR_WHILE_CREATING_CACHE_INSTANCE = "Error while creating cache instance. Using default. Message: {}.";
		public static final String ERROR_WHILE_CREATING_DATABASE_MANAGER_INSTANCE = "Error while creating database manager intance. Using default. Message: {}.";
		public static final String ERROR_WHILE_CREATING_MEDIA_REPOSITORY_INSTANCE = "Error while creating media repository instance. Using default. Message: {}.";
		public static final String ERROR_WHILE_LOADING_CACHE_CONFIGURATION = "Error while loading cache configuration. Using default. Message: {}.";
		public static final String ERROR_WHILE_LOADING_DATABASE_MANAGER_CONFIGURATION = 
				"Error while loading database manager configuration. Using default. Message: {}";
		public static final String ERROR_WHILE_LOADING_MEDIA_REPOSITORY_CONFIGURATION = 
				"Error while loading media repository configuration. Using default. Message:{}.";
		public static final String FOLLOW_NOT_FOUND_EXCEPTION = "Follow not found exception. Message:{}.";
		public static final String FRIENDSHIP_NOT_FOUND_EXCEPTION = "Friendship not found exception. Message:{}.";
		public static final String FRIENDSHIP_REQUEST_NOT_FOUND_EXCEPTION = "Friendship request not found exception. Message:{}.";
		public static final String INTERNAL_ERROR_EXCEPTION = "Internal error exception. Message:{}.";
		public static final String INVALID_PARAMETER_EXCEPTION = "Invalid parameter exception. Message:{}.";
		public static final String LOADED_ADMIN = "Loaded admin: {}";
		public static final String LOADING_ADMIN_CONFIGURATION = "Loading admin configuration";
		public static final String LOADING_AUTHENTICATION_PLUGIN =  "Loading authentication plugin";
		public static final String LOADING_AUTHORIZATION_PLUGIN = "Loading authorization plugin";
		public static final String LOADING_CACHE_CONFIGURATION = "Loading cache configuration {}.";
		public static final String LOADING_DATABASE_MANAGER_CONFIGURATION = "Loading database manager configuration {}";
		public static final String LOADING_MEDIA_REPOSITORY_CONFIGURATION = "Loading media repository configuration {}.";
		public static final String LOADING_FEED_POLICY = "Loading feed policy.";
		public static final String MEDIA_NOT_FOUND_EXCEPTION = "Media not found exception. Message:{}.";
		public static final String NO_CACHE_CONFIGURATION = "No cache configuration. Using default.";
		public static final String NO_DATABASE_MANAGER_CONFIGURATION = "No database manager configuration. Using default.";
		public static final String NO_MEDIA_REPOSITORY_CONFIGURATION = "No media repository configuration. Using default.";
		public static final String POST_NOT_FOUND_EXCEPTION = "Post not found exception. Message:{}.";
		public static final String RECEIVED_LOGIN_REQUEST = "Received login request.";
		public static final String RECEIVED_ACCEPT_FRIENDSHIP_REQUEST = "Received accept friendship request. Token:{}, username:{}.";
		public static final String RECEIVED_ADD_FOLLOW_REQUEST = "Received Add follow request. Token:{}, followed:{}.";
		public static final String RECEIVED_ADD_FOLLOW_ADMIN_REQUEST = "Received Add follow admin request. Token:{}, follower:{}, followed:{}.";
		public static final String RECEIVED_ADD_FRIENDSHIP_REQUEST = "Received Add friendship request. Token:{}, username:{}.";
		public static final String RECEIVED_ADD_FRIENDSHIP_ADMIN_REQUEST = 
				"Received Add friendship admin request. Token: {}, userId1: {}, userId2:{}.";
		public static final String RECEIVED_ADD_USER_REQUEST = "Received Add user request. username:{}, password:{}, profile desc:{}.";
		public static final String RECEIVED_ADD_USER_ADMIN_REQUEST = 
				"Received Add user admin request. Token: {}, username: {}, password: {}, profile desc:{}.";
		public static final String RECEIVED_CHANGE_PROFILE_PIC_REQUEST = "Received change profile pic request. Token:{}.";
		public static final String RECEIVED_CREATE_POST_REQUEST = 
				"Received Create post request. Token:{}, title:{}, content:{}, visibility:{}.";
		public static final String RECEIVED_DELETE_POST_REQUEST = "Received delete post request. Token: {}, postId: {}.";
		public static final String RECEIVED_FOLLOWS_REQUEST = "Received Follows request. Token:{}, User ID:{}.";
		public static final String RECEIVED_GET_FEED_POSTS_REQUEST = "Received get feed posts request. Token: {}.";
		public static final String RECEIVED_GET_FOLLOWED_USERS_REQUEST = "Received Get followed users request. Token:{}.";
		public static final String RECEIVED_GET_FOLLOW_RECOMMENDATIONS_REQUEST = "Received get follow recommendations request. Token:{}.";
		public static final String RECEIVED_GET_FOLLOWED_USERS_ADMIN_REQUEST = "Received Get followed users admin request. Token:{} , userId:{}.";
		public static final String RECEIVED_GET_FRIENDS_ADMIN_REQUEST = "Received Get friends admin request. Token:{}, userId:{}.";
		public static final String RECEIVED_GET_FRIENDS_POSTS_REQUEST = "Received Get friends posts request. Token:{}.";
		public static final String RECEIVED_GET_MEDIA_URI = "Received get media URI request. Token:{}, mediaId:{}.";
		public static final String RECEIVED_GET_RECEIVED_FRIENDSHIP_REQUESTS_REQUEST = "Received get received friendship requests request. Token:{}.";
		public static final String RECEIVED_GET_SELF_FRIENDS_REQUEST = "Received Get self friends. Token:{}.";
		public static final String RECEIVED_GET_SELF_POSTS_REQUEST = "Received Get self posts request. Token:{}.";
		public static final String RECEIVED_GET_SELF_REQUEST = "Received Get self request. Token:{}.";
		public static final String RECEIVED_GET_SENT_FRIENDSHIP_REQUESTS_REQUEST = "Received get sent friendship requests request. Token: {}.";
		public static final String RECEIVED_GET_USER_PROFILE_PIC = "Received get user pic. Token:{}, username:{}.";
		public static final String RECEIVED_GET_USER_POSTS_REQUEST = "Received Get user posts request. Token: {}, username: {}.";
		public static final String RECEIVED_GET_USER_RECOMMENDATIONS_REQUEST = "Received Get user recommendations request. Token:{}.";
		public static final String RECEIVED_GET_USER_SUMMARIES_REQUEST = "Received Get user summaries request. Token:{}.";
		public static final String RECEIVED_GET_USERS_ADMIN_REQUEST = "Received Get users admin request. Token: {}.";
		public static final String RECEIVED_GET_USERS_POSTS_ADMIN_REQUEST = "Received Get users posts admin request. Token:{} , userId:{}.";
		public static final String RECEIVED_IS_FRIEND_REQUEST = "Received Is Friend request. Token:{}, user ID: {}.";
		public static final String RECEIVED_REJECT_FRIENDSHIP_REQUEST = "Received reject friendship request. Token:{}, userId:{}.";
		public static final String RECEIVED_REMOVE_USER_ADMIN_REQUEST = "Received Remove user admin request. Token: {}, userId: {}.";
		public static final String RECEIVED_UNFOLLOW_REQUEST = "Received unfollow request. Token: {}, username: {}.";
		public static final String RECEIVED_UNFRIEND_REQUEST = "Received unfriend request. Token:{}, username: {}.";
		public static final String RECEIVED_UPDATE_USER_PROFILE = "Received update user profile request. Token:{}.";
		public static final String RECEIVED_USER_IS_ADMIN_REQUEST = "Received User is Admin request. User ID: {}.";
		public static final String USER_NOT_FOUND_EXCEPTION = "User not found exception. Message:{}.";
		public static final String FRIENDSHIP_REQUEST_ALREADY_RECEIVED_IGNORING_REQUEST = 
				"Friendship request from {} to {} already received. Ignoring request.";
	}
	
	public static class Exception {
		public static final String CANNOT_LOAD_BOOTSTRAP_PROPERTY = "Cannot load bootstrap property %s.";
		public static final String CANNOT_LOAD_LOCAL_PATH_PROPERTY = "Cannot load local path property %s.";
		public static final String CLASS_NOT_FOUND_ON_INSTANTIATION = "Could not find class %s. Message: %s.";
		public static final String COULD_NOT_LOAD_ADMIN_CONFIGURATION = "Could not load admin configuration. Message: %s";
		public static final String COULD_NOT_LOAD_CACHE_CONFIGURATION_PROPERTY = "Could not load cache configuration property %s.";
		public static final String ERROR_ON_INSTANTIATION = "Error while instantiating class %s. Message: %s."; 
		public static final String FRIENDSHIP_REQUEST_NOT_FOUND = "Friendship request not found. Token: %s, username: %s.";
		public static final String INVALID_CREDENTIALS = "Invalid credentials for user %s.";
		public static final String INVALID_PROPERTY = "Invalid property %s.";
		public static final String USER_IS_NOT_AUTHORIZED = "User %s is not authorized to perform operation %s.";
		public static final String COULD_NOT_FIND_USER = "Could not find user %s.";
		public static final String CONSTRUCTOR_NOT_FOUND_ON_INSTANTIATION = 
				"Could not find correct constructor of class %s. Message: %s.";
		public static final String USER_NOT_FOUND_EXCEPTION = "User not found exception.";
	}
}
