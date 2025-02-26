package com.armstrongmsg.socialnet.storage.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.ClassFactory;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class MediaRepositoryFactory {
	private static Logger logger = LoggerFactory.getLogger(MediaRepositoryFactory.class);
	private ClassFactory classFactory;

	public MediaRepositoryFactory() {
		classFactory = new ClassFactory();
	}
	
	public MediaRepositoryFactory(ClassFactory classFactory) {
		this.classFactory = classFactory;
	}

	public MediaRepository loadMediaRepositoryFromConfiguration() throws FatalErrorException {
		String mediaRepositoryClassName = null;

		try {
			PropertiesUtil properties = PropertiesUtil.getInstance();
			mediaRepositoryClassName = properties.getProperty(ConfigurationProperties.MEDIA_REPOSITORY_CLASS_NAME);
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_LOADING_MEDIA_REPOSITORY_CONFIGURATION, e.getMessage());
			return new LocalFileSystemMediaRepository();
		}

		try {
			if (mediaRepositoryClassName == null || mediaRepositoryClassName.isEmpty()) {
				logger.info(Messages.Logging.NO_MEDIA_REPOSITORY_CONFIGURATION);
				return new LocalFileSystemMediaRepository();
			}
			logger.info(Messages.Logging.LOADING_MEDIA_REPOSITORY_CONFIGURATION, mediaRepositoryClassName);
			return (MediaRepository) classFactory.createInstance(mediaRepositoryClassName);
		} catch (FatalErrorException e) {
			logger.error(Messages.Logging.ERROR_WHILE_CREATING_MEDIA_REPOSITORY_INSTANCE, e.getMessage());
			return new LocalFileSystemMediaRepository();
		}
	}
}
