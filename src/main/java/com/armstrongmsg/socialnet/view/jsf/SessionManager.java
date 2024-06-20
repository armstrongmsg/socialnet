package com.armstrongmsg.socialnet.view.jsf;

import com.armstrongmsg.socialnet.model.authentication.UserToken;

public class SessionManager {
	private static Session currentSession;
	
	public static Session getCurrentSession() {
		return currentSession;
	}
	
	public static void setCurrentSession(Session session) {
		currentSession = session;
	}

	public static void startSession(UserToken token, boolean userIsAdmin) {
		Session session = new Session(token);
		session.setAdmin(userIsAdmin);
		session.setLogged(true);
		setCurrentSession(session);
	}
}
