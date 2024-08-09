package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.User;
import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;

public class DefaultUserRepository implements UserRepository {
	private DatabaseConnectionManager connectionManager;
	
	public DefaultUserRepository(DatabaseConnectionManager em) {
		this.connectionManager = em;
	}
	
	@Override
	public User getUserById(String id) {
		List<User> result = new DatabaseOperation<List<User>>(connectionManager).
				setQueryString("SELECT c FROM User c WHERE c.userId = :userId").
				setParameter("userId", id).query();
		
		if (result.size() >= 1) {
			return result.get(0);
		}
		
		return null;
	}

	@Override
	public User getUserByUsername(String username) {
		List<User> result = new DatabaseOperation<List<User>>(connectionManager).
				setQueryString("SELECT c FROM User c WHERE c.username = :username").
				setParameter("username", username).query();
		
		if (result.size() >= 1) {
			return result.get(0);
		}
		
		return null;
	}

	@Override
	public void saveUser(User user) {
		new DatabaseOperation<User>(connectionManager).persist(user);
	}
	
	@Override
	public void updateUser(User user) {
		new DatabaseOperation<User>(connectionManager).merge(user);
	}

	@Override
	public List<User> getAllUsers() {
		return new DatabaseOperation<List<User>>(connectionManager).
				setQueryString("SELECT c FROM User c").query();
	}

	@Override
	public void removeUserById(String id) {
		new DatabaseOperation<User>(connectionManager).remove(getUserById(id));
	}
}
