package com.armstrongmsg.socialnet.storage.database.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DatabaseConnectionManager {
	private static final String DATABASE_PERSISTENCE_UNIT = "default";
	
	private EntityManagerFactory emf;
	
	public EntityManager getEntityManager() {
		this.emf = Persistence.createEntityManagerFactory(DATABASE_PERSISTENCE_UNIT);
		return emf.createEntityManager();
	}

	public void close() {
		this.emf.close();
		this.emf = null;
	}
}
