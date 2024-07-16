package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.armstrongmsg.socialnet.model.FriendshipRequest;

public class DefaultFriendshipRequestRepository implements FriendshipRequestRepository {

	@Override
	public void add(FriendshipRequest friendshipRequest) {
		// FIXME constant
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.persist(friendshipRequest);
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
	public List<FriendshipRequest> getSentFriendshipRequestsById(String userId) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT f FROM FriendshipRequest f WHERE f.requester.userId = :userId")
						.setParameter("userId", userId);
			return query.getResultList();
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public List<FriendshipRequest> getReceivedFriendshipRequestsById(String userId) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId")
						.setParameter("userId", userId);
			return query.getResultList();
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public FriendshipRequest getReceivedFriendshipRequestById(String userId, String username) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery(
					"SELECT f FROM FriendshipRequest f WHERE f.requested.userId = :userId and f.requester.username = :username")
						.setParameter("userId", userId).setParameter("username", username);
			return (FriendshipRequest) query.getResultList().get(0);
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public void removeFriendshipRequest(FriendshipRequest friendshipRequest) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.remove(em.contains(friendshipRequest) ? friendshipRequest : em.merge(friendshipRequest));
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
