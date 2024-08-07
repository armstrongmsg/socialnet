package com.armstrongmsg.socialnet.view.jsf;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armstrongmsg.socialnet.core.ApplicationFacade;

@WebListener
public class SocialnetService implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(SocialnetService.class);

    public void contextInitialized(ServletContextEvent event) {
    	logger.info("Starting up.");
    	
    	ApplicationFacade.getInstance();
    }

    public void contextDestroyed(ServletContextEvent event) {
    	System.out.println("Shutting down.");
    }
}
