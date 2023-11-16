package com.armstrongmsg.socialnet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.armstrongmsg.socialnet.constants.SystemConstants;

public class PropertiesUtil {
	private Properties properties;
	
	public PropertiesUtil() throws FileNotFoundException, IOException {
		properties = new Properties();
		String configurationPath = SystemConstants.PROJECT_BASE_PATH + 
				File.pathSeparator + SystemConstants.CONFIGURATION_PATH; 
		properties.load(new FileInputStream(configurationPath));
	}
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
	public String getProperty(String propertyName, String propertyDefaultValue) {
		return properties.getProperty(propertyName, propertyDefaultValue);
	}
}
