package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.exceptions.InternalErrorException;
import com.armstrongmsg.socialnet.exceptions.UserAlreadyExistsException;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class Bootstrap {
	private static Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	private List<String> bootstrapUsernames;
	private List<String> bootstrapUserDescriptions;
	private List<String> bootstrapUserPasswords;
	
	public Bootstrap() throws FatalErrorException {
		String bootstrapUsersProperty = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.BOOTSTRAP_USERS);

		if (bootstrapUsersProperty == null || bootstrapUsersProperty.isEmpty()) {
			throw new FatalErrorException(String.format(Messages.Exception.CANNOT_LOAD_BOOTSTRAP_PROPERTY,
					ConfigurationProperties.BOOTSTRAP_USERS));
		}

		this.bootstrapUsernames = Arrays.asList(bootstrapUsersProperty.split(SystemConstants.BOOTSTRAP_USERS_SEPARATOR));
		this.bootstrapUserDescriptions = new ArrayList<String>();
		this.bootstrapUserPasswords = new ArrayList<String>();
		
		for (String bootstrapUsername : bootstrapUsernames) {
			if (bootstrapUsername.isEmpty()) {
				throw new FatalErrorException(String.format(Messages.Exception.CANNOT_LOAD_BOOTSTRAP_PROPERTY,
						ConfigurationProperties.BOOTSTRAP_USERS));
			}
		 	this.bootstrapUserDescriptions.add(getUserProfileDescription(bootstrapUsername));
			this.bootstrapUserPasswords.add(getUserPassword(bootstrapUsername));
		}
	}

	private String getUserProfileDescription(String bootstrapUsername)
			throws FatalErrorException {
		return getPropertyOrFail(ConfigurationProperties.BOOTSTRAP_DESCRIPTION + bootstrapUsername);
	}

	private String getUserPassword(String bootstrapUsername) throws FatalErrorException {
		return getPropertyOrFail(ConfigurationProperties.BOOTSTRAP_PASSWORD + bootstrapUsername);
	}
	
	private String getPropertyOrFail(String propertyName) throws FatalErrorException {
		String value = PropertiesUtil.getInstance().getProperty(propertyName);
		
		if (value == null) {
			throw new FatalErrorException(String.format(Messages.Exception.CANNOT_LOAD_BOOTSTRAP_PROPERTY,
					propertyName));
		}
		
		return value;
	}
	
	public void startNetwork(Network network) throws FatalErrorException {
		for (int i = 0; i < bootstrapUsernames.size(); i++) {
			logger.debug("Creating user:{},{}", bootstrapUsernames.get(i), bootstrapUserDescriptions.get(i));
			try {
				network.addUser(bootstrapUsernames.get(i), bootstrapUserPasswords.get(i), bootstrapUserDescriptions.get(i));
			} catch (InternalErrorException e) {
				throw new FatalErrorException(e.getMessage());
			} catch (UserAlreadyExistsException e) {
				throw new FatalErrorException(e.getMessage());
			}
		}
	}
}
