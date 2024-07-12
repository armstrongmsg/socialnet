package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import com.armstrongmsg.socialnet.model.User;

public interface UserRepository {
	User getUserById(String id);
	User getUserByUsername(String username);
	void saveUser(User user);
	List<User> getAllUsers();
	void removeUserById(String id);
	void updateUser(User user);
}
