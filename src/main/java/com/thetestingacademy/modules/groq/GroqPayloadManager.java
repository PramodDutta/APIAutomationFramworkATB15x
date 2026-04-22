package com.thetestingacademy.modules.groq;

import com.google.gson.Gson;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.pojos.groq.requestPOJO.ChatCompletionRequest;
import com.thetestingacademy.pojos.groq.requestPOJO.Message;
import com.thetestingacademy.pojos.groq.responsePojo.ChatCompletionResponse;
import com.thetestingacademy.pojos.groq.responsePojo.GroqErrorResponse;

public class GroqPayloadManager {

    private final Gson gson = new Gson();

    /** Build a single-user-turn chat request payload for Groq's OpenAI-compatible endpoint. */
    public String buildChatRequest(String model, String userPrompt) {
        ChatCompletionRequest req = new ChatCompletionRequest();
        req.setModel(model == null ? APIConstants.GROQ_DEFAULT_MODEL : model);
        req.addMessage(new Message("user", userPrompt));
        return gson.toJson(req);
    }

    public String buildChatRequest(String userPrompt) {
        return buildChatRequest(APIConstants.GROQ_DEFAULT_MODEL, userPrompt);
    }

    /** Deserialize a successful chat.completion response. */
    public ChatCompletionResponse parseChatResponse(String json) {
        return gson.fromJson(json, ChatCompletionResponse.class);
    }

    /** Deserialize a Groq error body (used on 4xx/5xx). */
    public GroqErrorResponse parseErrorResponse(String json) {
        return gson.fromJson(json, GroqErrorResponse.class);
    }
}
