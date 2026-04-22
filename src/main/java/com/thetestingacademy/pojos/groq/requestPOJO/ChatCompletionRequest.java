package com.thetestingacademy.pojos.groq.requestPOJO;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class ChatCompletionRequest {
    @Expose
    private String model;
    @Expose
    private List<Message> messages;

    public ChatCompletionRequest() {
        this.messages = new ArrayList<>();
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public void addMessage(Message m) { this.messages.add(m); }
}
