package com.armstrongmsg.socialnet.util;

import java.io.File;

import com.armstrongmsg.socialnet.constants.SystemConstants;

public class ApplicationPaths {
	// returns a path similar to "/usr/local/tomcat/webapps/socialnet/WEB-INF/classes"
	public static String getApplicationClasspath() {
		return Thread.currentThread().getContextClassLoader().getResource("").getPath();
	}
	
	public static String getApplicationPropertiesPath() {
		return getApplicationClasspath() + File.separator + SystemConstants.APPLICATION_PROPERTIES_FILE_NAME;
	}
	
	public static String getApplicationBasePath() {
		return getApplicationClasspath().split("WEB-INF/classes")[0];
	}
}
