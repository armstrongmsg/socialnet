package com.armstrongmsg.socialnet.core.authorization;

public class Operation {
	private OperationType operation;

	public Operation(OperationType operation) {
		this.operation = operation;
	}

	public OperationType getOperation() {
		return operation;
	}

	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
}