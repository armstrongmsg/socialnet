package com.armstrongmsg.socialnet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;

public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	
	private static PropertiesUtil instance;
	
	private Properties properties;
	
	public static PropertiesUtil getInstance() throws FatalErrorException {
		if (instance == null) {
			try {
				instance = new PropertiesUtil();
			} catch (FileNotFoundException e) {
				logger.error(Messages.Logging.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage());
				throw new FatalErrorException(
						String.format(Messages.Exception.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage()));
			} catch (IOException e) {
				logger.error(Messages.Logging.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage());
				throw new FatalErrorException(
						String.format(Messages.Exception.COULD_NOT_LOAD_ADMIN_CONFIGURATION, e.getMessage()));
			}
		}
		
		return instance;
	}
	
	private PropertiesUtil() throws FileNotFoundException, IOException {
		properties = new Properties();
		String configurationPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + 
				File.separator + "application.properties";
		properties.load(new FileInputStream(configurationPath));
	}
	
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	
	public String getProperty(String propertyName, String propertyDefaultValue) {
		return properties.getProperty(propertyName, propertyDefaultValue);
	}
}
