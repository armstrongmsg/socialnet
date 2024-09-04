package com.armstrongmsg.socialnet.view.jsf.bean;

import javax.faces.FacesException;

public class JsfExceptionHandler {
	public void handle(Exception e) {
		throw new FacesException(e);
	}
}
