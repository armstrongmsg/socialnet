package com.armstrongmsg.socialnet.storage.database.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.armstrongmsg.socialnet.storage.database.connection.DatabaseConnectionManager;

public class DatabaseOperation<T> {
	private DatabaseConnectionManager databaseConnectionManager;
	private String queryString;
	private Map<String, String> parameters;
	
	public DatabaseOperation(DatabaseConnectionManager connectionManager) {
		this.databaseConnectionManager = connectionManager;
		parameters = new HashMap<String, String>();
	}
	
	@SuppressWarnings("unchecked")
	public T query() {
		EntityManager em = this.databaseConnectionManager.getEntityManager();
		
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		
		try {
			Query query = em.createQuery(queryString);
			
			for (String key : parameters.keySet()) {
				query.setParameter(key, parameters.get(key));
			}
			
			T result = (T) query.getResultList();
			em.getTransaction().rollback();
			return result;
		} finally {
			em.close();
		}
	}
	
	public void persist(T entity) {
		EntityManager em = this.databaseConnectionManager.getEntityManager();
		
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		
		try {
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
		}
	}
	
	public void merge(T entity) {
		EntityManager em = this.databaseConnectionManager.getEntityManager();

		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		
		try {
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
		}
	}
	
	public void remove(T entity) {
		EntityManager em = this.databaseConnectionManager.getEntityManager();
		
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}
		
		try {
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
