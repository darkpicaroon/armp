package com.android.armp.model;

public class ObjectResponse {

	private boolean logged;
	private int status;
	private Object obj;
	
	public ObjectResponse(int status, boolean logged) {
		this.status = status;
		this.logged = logged;
	}

	public boolean isLogged() {
		return logged;
	}

	public int getStatus() {
		return status;
	}
	
	public void setObject(Object obj) {
		this.obj = obj;
	}

	public Object getObject() {
		return obj;
	}
	
	
}
