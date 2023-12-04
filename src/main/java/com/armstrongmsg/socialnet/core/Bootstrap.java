package com.armstrongmsg.socialnet.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.Messages;
import com.armstrongmsg.socialnet.constants.SystemConstants;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.model.Network;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class Bootstrap {
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
			network.addUser(bootstrapUsernames.get(i), bootstrapUserPasswords.get(i), bootstrapUserDescriptions.get(i));
		}
	}
}
