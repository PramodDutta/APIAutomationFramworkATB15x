package com.thetestingacademy.pojos.groq.requestPOJO;

import com.google.gson.annotations.Expose;

public class Message {
    @Expose
    private String role;
    @Expose
    private String content;

    public Message() {}

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
