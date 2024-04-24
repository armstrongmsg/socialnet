package com.armstrongmsg.socialnet.constants;

public class Messages {
	public static class Logging {
		public static final String LOADING_ADMIN_CONFIGURATION = "Loading admin configuration";
		public static final String LOADING_AUTHENTICATION_PLUGIN =  "Loading authentication plugin";
		public static final String LOADING_AUTHORIZATION_PLUGIN = "Loading authorization plugin";
		public static final String LOADED_ADMIN = "Loaded admin: {}";
		public static final String COULD_NOT_LOAD_ADMIN_CONFIGURATION = "Could not load admin configuration. Message: {}";
	}
	
	public static class Exception {
		public static final String CANNOT_LOAD_BOOTSTRAP_PROPERTY = "Cannot load bootstrap property %s.";
		public static final String CLASS_NOT_FOUND_ON_INSTANTIATION = "Could not find class %s. Message: %s.";
		public static final String COULD_NOT_LOAD_ADMIN_CONFIGURATION = "Could not load admin configuration. Message: {}";
		public static final String ERROR_ON_INSTANTIATION = "Error while instantiating class %s. Message: %s."; 
		public static final String INVALID_CREDENTIALS = "Invalid credentials for user %s.";
		public static final String USER_IS_NOT_AUTHORIZED = "User %s is not authorized to perform operation %s.";
		public static final String COULD_NOT_FIND_USER = "Could not find user %s.";
		public static final String CONSTRUCTOR_NOT_FOUND_ON_INSTANTIATION = 
				"Could not find correct constructor of class %s. Message: %s.";
	}
}
