package com.armstrongmsg.socialnet.view.jsf;

public class SessionManager {
	private static Session currentSession;
	
	public static Session getCurrentSession() {
		return currentSession;
	}
	
	public static void setCurrentSession(Session session) {
		currentSession = session;
	}
}
