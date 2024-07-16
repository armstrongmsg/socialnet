package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.armstrongmsg.socialnet.model.Friendship;

public class DefaultFriendshipRepository implements FriendshipRepository {

	@Override
	public List<Friendship> getFriendshipsByUserId(String userId) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT f FROM Friendship f WHERE f.friend1_id = :userid or f.friend2_id = :userid")
						.setParameter("userid", userId);
			return query.getResultList();
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public void saveFriendship(Friendship friendship) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.persist(friendship);
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