package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.armstrongmsg.socialnet.model.Follow;

public class DefaultFollowRepository implements FollowRepository {

	@Override
	public List<Follow> getFollowsByUserId(String userId) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery("SELECT f FROM Follow f WHERE f.follower_id = :userid or f.followed_id = :userid")
						.setParameter("userid", userId);
			return query.getResultList();
		} finally {
			em.close();
			emf.close();
		}
	}

	@Override
	public void saveFollow(Follow follow) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.persist(follow);
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
	public void removeFollow(Follow follow) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
		EntityManager em = emf.createEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.remove(follow);
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
