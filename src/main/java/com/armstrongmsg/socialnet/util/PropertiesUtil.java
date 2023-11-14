package com.armstrongmsg.socialnet.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
	private Properties properties;
	
	public PropertiesUtil() throws FileNotFoundException, IOException {
		properties = new Properties();
		// FIXME hardcoded
		properties.load(new FileInputStream("/usr/local/tomcat/webapps/socialnet/resources/application.properties"));
	}
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
}
