package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class DatabaseOperation<T> {
	private DatabaseConnectionManager connectionManager;
	private String queryString;
	private Map<String, String> parameters;
	
	public DatabaseOperation() {
		connectionManager = new DatabaseConnectionManager();
		parameters = new HashMap<String, String>();
	}
	
	@SuppressWarnings("unchecked")
	public T query() {
		EntityManager em = this.connectionManager.getEntityManager();
		
		try {
			em.getTransaction().begin();
			Query query = em.createQuery(queryString);
			
			for (String key : parameters.keySet()) {
				query.setParameter(key, parameters.get(key));
			}
			
			return (T) query.getResultList();
		} finally {
			em.close();
			connectionManager.close();
		}
	}
	
	public void persist(T entity) {
		EntityManager em = this.connectionManager.getEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.persist(entity);
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			connectionManager.close();
		}
	}
	
	public void merge(T entity) {
		EntityManager em = this.connectionManager.getEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;
			try {
				em.merge(entity);
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			connectionManager.close();
		}
	}
	
	public void remove(T entity) {
		EntityManager em = this.connectionManager.getEntityManager();
		
		try {
			em.getTransaction().begin();
			boolean committed = false;

			try {
				em.remove(em.contains(entity) ? entity : em.merge(entity));
				em.getTransaction().commit();
				committed = true;
			} finally {
				if (!committed)
					em.getTransaction().rollback();
			}
		} finally {
			em.close();
			connectionManager.close();
		}
	}
	
	public DatabaseOperation<T> setQueryString(String queryStr) {
		this.queryString = queryStr;
		return this;
	}
	
	public DatabaseOperation<T> setParameter(String parameterName, String parameterValue) {
		parameters.put(parameterName, parameterValue);
		return this;
	}
}
