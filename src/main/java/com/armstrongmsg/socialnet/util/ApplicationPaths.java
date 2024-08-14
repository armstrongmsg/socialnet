package com.armstrongmsg.socialnet.util;

import java.io.File;

public class ApplicationPaths {
	public static String getApplicationPath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getPath();
	}
	
	public static String getApplicationPropertiesPath() {
		// FIXME constant
		return getApplicationPath() + File.separator + "application.properties";
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
