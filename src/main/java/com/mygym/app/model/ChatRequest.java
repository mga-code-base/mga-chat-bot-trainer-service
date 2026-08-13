package com.mygym.app.model;

public class ChatRequest {
    private String message;
    private String mode;
    private String sessionId;
    
	public String getMessage() { return message;	}
	public void setMessage(String message) { this.message = message; }
	
	public String getMode() { return mode; }
	public void setMode(String mode) { this.mode = mode; }
	
	public String getSessionId() { return sessionId; }
	public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}