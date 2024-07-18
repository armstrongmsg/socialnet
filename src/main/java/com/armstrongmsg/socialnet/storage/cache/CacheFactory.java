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
	
	public Cache loadCacheFromConfiguration() {
		try {
			PropertiesUtil properties = PropertiesUtil.getInstance();
			String cacheClassName = properties.getProperty(ConfigurationProperties.CACHE_CLASS_NAME);
			
			if (cacheClassName == null || cacheClassName.isEmpty()) {
				// TODO test
				logger.info(Messages.Logging.NO_CACHE_CONFIGURATION);
				return new DefaultCache();
			}
			
			logger.info(Messages.Logging.LOADING_CACHE_CONFIGURATION, cacheClassName);
			ClassFactory classFactory = new ClassFactory();
			return (Cache) classFactory.createInstance(cacheClassName);
			// TODO test
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_LOADING_CACHE_CONFIGURATION, e.getMessage());
			return new DefaultCache();
		}
	}
}
