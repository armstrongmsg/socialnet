package com.armstrongmsg.socialnet.model.authorization;

import com.armstrongmsg.socialnet.model.User;

public class OperationOnUser extends Operation {
	private User target;
	
	public OperationOnUser(OperationType operation, User target) {
		super(operation);
		this.target = target;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}
}
