package com.armstrongmsg.socialnet.storage.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class CacheFactory {
	private static Logger logger = LoggerFactory.getLogger(CacheFactory.class);
	
	private ClassFactory classFactory;
	
	public CacheFactory() {
		this.classFactory = new ClassFactory();
	}
	
	public CacheFactory(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}
	
	public Cache loadCacheFromConfiguration() {
		String cacheClassName = null;
		
		try {
			PropertiesUtil properties = PropertiesUtil.getInstance();
			cacheClassName = properties.getProperty(ConfigurationProperties.CACHE_CLASS_NAME);
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_LOADING_CACHE_CONFIGURATION, e.getMessage());
			return new DefaultCache();
		}
			
		try {
			if (cacheClassName == null || cacheClassName.isEmpty()) {
				logger.info(Messages.Logging.NO_CACHE_CONFIGURATION);
				return new DefaultCache();
			}
			logger.info(Messages.Logging.LOADING_CACHE_CONFIGURATION, cacheClassName);
			return (Cache) classFactory.createInstance(cacheClassName);
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_CREATING_CACHE_INSTANCE, e.getMessage());
			return new DefaultCache();
		}
	}
}
