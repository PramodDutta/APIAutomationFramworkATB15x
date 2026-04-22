package com.thetestingacademy.pojos.groq.responsePojo;

import java.util.List;

public class ChatCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    public String getId() { return id; }
    public String getObject() { return object; }
    public Long getCreated() { return created; }
    public String getModel() { return model; }
    public List<Choice> getChoices() { return choices; }
    public Usage getUsage() { return usage; }

    public String firstMessageContent() {
        if (choices == null || choices.isEmpty() || choices.get(0).getMessage() == null) return null;
        return choices.get(0).getMessage().getContent();
    }
}
