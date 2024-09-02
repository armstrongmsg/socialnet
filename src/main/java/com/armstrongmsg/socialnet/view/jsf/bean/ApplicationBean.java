package com.armstrongmsg.socialnet.view.jsf.bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.armstrongmsg.socialnet.core.ApplicationFacade;

@ManagedBean(name = "applicationBean", eager = true)
@ApplicationScoped
public class ApplicationBean {
	private ApplicationFacade facade;
	
	public ApplicationBean() {
		
	}
	
	ApplicationBean(ApplicationFacade facade) {
		this.facade = facade;
	}
	
	@PostConstruct
	public void initialize() { 
		facade = ApplicationFacade.getInstance();
	}

	public ApplicationFacade getFacade() {
		return facade;
	}
}
