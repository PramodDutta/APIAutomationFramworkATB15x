package com.thetestingacademy.pojos.groq.responsePojo;

public class ChatMessage {
    private String role;
    private String content;
    private String reasoning;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}
