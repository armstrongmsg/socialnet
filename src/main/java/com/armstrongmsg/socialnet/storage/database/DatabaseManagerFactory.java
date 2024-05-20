package com.armstrongmsg.socialnet.storage.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class DatabaseManagerFactory {
	private static Logger logger = LoggerFactory.getLogger(DatabaseManagerFactory.class);
	
	public DatabaseManager loadDatabaseManagerFromConfiguration() {
		try {
			PropertiesUtil properties = PropertiesUtil.getInstance();
			String databaseManagerClassName = properties.getProperty(ConfigurationProperties.DATABASE_MANAGER_CLASS_NAME);
			
			if (databaseManagerClassName == null || databaseManagerClassName.isEmpty()) {
				logger.info(Messages.Logging.NO_DATABASE_MANAGER_CONFIGURATION);
				return new DefaultDatabaseManager();
			}
			
			logger.info(Messages.Logging.LOADING_DATABASE_MANAGER_CONFIGURATION, databaseManagerClassName);
			ClassFactory classFactory = new ClassFactory();
			return (DatabaseManager) classFactory.createInstance(databaseManagerClassName);
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_LOADING_DATABASE_MANAGER_CONFIGURATION, e.getMessage());
			return new DefaultDatabaseManager();
		}
	}
}
