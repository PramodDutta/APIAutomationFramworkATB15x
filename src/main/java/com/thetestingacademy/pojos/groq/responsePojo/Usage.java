package com.thetestingacademy.pojos.groq.responsePojo;

import com.google.gson.annotations.SerializedName;

public class Usage {
    @SerializedName("queue_time")
    private Double queueTime;
    @SerializedName("prompt_tokens")
    private Integer promptTokens;
    @SerializedName("prompt_time")
    private Double promptTime;
    @SerializedName("completion_tokens")
    private Integer completionTokens;
    @SerializedName("completion_time")
    private Double completionTime;
    @SerializedName("total_tokens")
    private Integer totalTokens;
    @SerializedName("total_time")
    private Double totalTime;

    public Double getQueueTime() { return queueTime; }
    public Integer getPromptTokens() { return promptTokens; }
    public Double getPromptTime() { return promptTime; }
    public Integer getCompletionTokens() { return completionTokens; }
    public Double getCompletionTime() { return completionTime; }
    public Integer getTotalTokens() { return totalTokens; }
    public Double getTotalTime() { return totalTime; }
}
