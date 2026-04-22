package com.thetestingacademy.pojos.groq.responsePojo;

import com.google.gson.annotations.SerializedName;

public class Choice {
    private Integer index;
    private ChatMessage message;
    private Object logprobs;
    @SerializedName("finish_reason")
    private String finishReason;

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public ChatMessage getMessage() { return message; }
    public void setMessage(ChatMessage message) { this.message = message; }

    public Object getLogprobs() { return logprobs; }
    public void setLogprobs(Object logprobs) { this.logprobs = logprobs; }

    public String getFinishReason() { return finishReason; }
    public void setFinishReason(String finishReason) { this.finishReason = finishReason; }
}
