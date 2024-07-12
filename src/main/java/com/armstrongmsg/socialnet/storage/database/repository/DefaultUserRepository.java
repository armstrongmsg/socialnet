package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.armstrongmsg.socialnet.model.User;

public class DefaultUserRepository implements UserRepository {
	
	@Override
	public User getUserById(String id) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT c FROM User c WHERE c.userid = :userid")
						.setParameter("userid", id);
			List<User> result = query.getResultList();
			return result.get(0);
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public User getUserByUsername(String username) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT c FROM User c WHERE c.username = :username")
						.setParameter("username", username);
			List<User> result = query.getResultList();
			return result.get(0);
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public void saveUser(User user) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.persist(user);
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			emf.close();
		}
	}
	
	@Override
	public void updateUser(User user) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.merge(user);
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public List<User> getAllUsers() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT c FROM User c");
			List<User> result = query.getResultList();
			return result;				
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public void removeUserById(String id) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;

			try {
				em.remove(getUserById(id));
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			emf.close();
		}
	}
}
