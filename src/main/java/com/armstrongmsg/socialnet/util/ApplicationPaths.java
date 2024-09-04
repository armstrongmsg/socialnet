package com.armstrongmsg.socialnet.util;

import java.io.File;

import com.armstrongmsg.socialnet.constants.SystemConstants;

public class ApplicationPaths {
	public static String getApplicationPath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getPath();
	}
	
	public static String getApplicationPropertiesPath() {
		return getApplicationPath() + File.separator + SystemConstants.APPLICATION_PROPERTIES_FILE_NAME;
	}
	
	public static String getProjectPath() {
		// FIXME constant
		return "/usr/local/tomcat/webapps/socialnet/";
	}
	
	public static String getApplicationImageCacheDirectoryName() {
		// FIXME constant
		return "db";
	}
	
	public static String getApplicationImageCachePath() {
		return getProjectPath() + getApplicationImageCacheDirectoryName();
	}
}
