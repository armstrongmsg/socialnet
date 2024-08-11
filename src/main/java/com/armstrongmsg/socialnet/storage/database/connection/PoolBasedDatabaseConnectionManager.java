package com.armstrongmsg.socialnet.storage.database.connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.armstrongmsg.socialnet.constants.ConfigurationProperties;
import com.armstrongmsg.socialnet.constants.ConfigurationPropertiesDefaults;
import com.armstrongmsg.socialnet.exceptions.FatalErrorException;
import com.armstrongmsg.socialnet.util.PropertiesUtil;

public class PoolBasedDatabaseConnectionManager implements DatabaseConnectionManager {
	private EntityManagerFactory emFactory;
	
	public PoolBasedDatabaseConnectionManager(String persistenceUnit) {
		this.emFactory = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	public PoolBasedDatabaseConnectionManager() throws FatalErrorException {
		String persistenceUnit = ConfigurationPropertiesDefaults.DATABASE_PERSISTENCE_UNIT;
		
		try {
			persistenceUnit = PropertiesUtil.getInstance().getProperty(ConfigurationProperties.DATABASE_PERSISTENCE_UNIT, 
					ConfigurationPropertiesDefaults.DATABASE_PERSISTENCE_UNIT);
		} catch (FatalErrorException | NumberFormatException e) {
			throw new FatalErrorException();
		}
		
		this.emFactory = Persistence.createEntityManagerFactory(persistenceUnit);
	}
	
	public EntityManager getEntityManager() {
		return this.emFactory.createEntityManager();
	}

	public void close() {
		this.emFactory.close();
	}
}
