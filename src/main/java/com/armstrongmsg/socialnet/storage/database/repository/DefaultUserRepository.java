package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.User;

public class DefaultUserRepository implements UserRepository {
	
	@Override
	public User getUserById(String id) {
		return new DatabaseOperation<List<User>>().
				setQueryString("SELECT c FROM User c WHERE c.userId = :userId").
				setParameter("userId", id).query().get(0);
	}

	@Override
	public User getUserByUsername(String username) {
		return new DatabaseOperation<List<User>>().
				setQueryString("SELECT c FROM User c WHERE c.username = :username").
				setParameter("username", username).query().get(0); 
	}

	@Override
	public void saveUser(User user) {
		new DatabaseOperation<User>().persist(user);
	}
	
	@Override
	public void updateUser(User user) {
		new DatabaseOperation<User>().merge(user);
	}

	@Override
	public List<User> getAllUsers() {
		return new DatabaseOperation<List<User>>().
				setQueryString("SELECT c FROM User c").query();
	}

	@Override
	public void removeUserById(String id) {
		new DatabaseOperation<User>().remove(getUserById(id));
	}
}
