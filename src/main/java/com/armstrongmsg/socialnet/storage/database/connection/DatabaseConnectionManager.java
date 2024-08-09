package com.armstrongmsg.socialnet.storage.database.connection;

import javax.persistence.EntityManager;

public interface DatabaseConnectionManager {
	EntityManager getEntityManager();
	void close();
}
