package com.thetestingacademy.tests.individual.groq;

import com.thetestingacademy.asserts.AssertActions;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.modules.groq.GroqPayloadManager;
import com.thetestingacademy.pojos.groq.responsePojo.ChatCompletionResponse;
import com.thetestingacademy.pojos.groq.responsePojo.GroqErrorResponse;
import com.thetestingacademy.utils.EnvUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGroqChatCompletion {

    private static final Logger logger = LogManager.getLogger(TestGroqChatCompletion.class);

    private RequestSpecification requestSpecification;
    private GroqPayloadManager groqPayloadManager;
    private AssertActions assertActions;

    @BeforeClass
    public void setup() {
        logger.info("=== Setting up Groq test ===");
        groqPayloadManager = new GroqPayloadManager();
        assertActions = new AssertActions();

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(APIConstants.GROQ_BASE_URL)
                .setBasePath(APIConstants.GROQ_CHAT_COMPLETIONS_URL)
                .addHeader("Authorization", "Bearer " + EnvUtil.getGroqApiKey())
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test(groups = "qa")
    @Owner("Promode")
    @Description("TC#GROQ1 - POST chat completion with a user prompt returns 200 + non-empty assistant content")
    public void test_groq_chat_completion_positive() {
        String payload = groqPayloadManager.buildChatRequest("Write a selenium java code for amazon login.");
        logger.debug("Groq request payload: {}", payload);

        Response response = RestAssured.given(requestSpecification)
                .body(payload)
                .when()
                .post();

        logger.info("Groq response status: {}", response.statusCode());
        assertActions.verifyStatusCode(response, 200);

        ChatCompletionResponse parsed = groqPayloadManager.parseChatResponse(response.asString());
        assertThat(parsed.getId()).as("response id").isNotBlank();
        assertThat(parsed.getObject()).as("object kind").isEqualTo("chat.completion");
        assertThat(parsed.getModel()).as("model echoed").contains("gpt-oss");
        assertThat(parsed.getChoices()).as("choices list").isNotEmpty();

        String content = parsed.firstMessageContent();
        assertThat(content).as("assistant content").isNotBlank();
        assertThat(parsed.getChoices().get(0).getFinishReason()).isEqualTo("stop");

        assertThat(parsed.getUsage()).as("usage block").isNotNull();
        assertThat(parsed.getUsage().getTotalTokens()).isPositive();

        logger.info("Groq returned {} completion tokens, total {} tokens",
                parsed.getUsage().getCompletionTokens(), parsed.getUsage().getTotalTokens());
    }

    @Test(groups = "qa")
    @Owner("Promode")
    @Description("TC#GROQ2 - POST with an invalid API key returns 401 with a structured error body")
    public void test_groq_chat_completion_invalid_key() {
        RequestSpecification badSpec = new RequestSpecBuilder()
                .setBaseUri(APIConstants.GROQ_BASE_URL)
                .setBasePath(APIConstants.GROQ_CHAT_COMPLETIONS_URL)
                .addHeader("Authorization", "Bearer gsk_obviously_invalid_key_for_testing")
                .setContentType(ContentType.JSON)
                .build();

        String payload = groqPayloadManager.buildChatRequest("Hello");
        Response response = RestAssured.given(badSpec).body(payload).when().post();

        assertThat(response.statusCode()).as("unauthorized status")
                .isIn(401, 403);

        GroqErrorResponse err = groqPayloadManager.parseErrorResponse(response.asString());
        assertThat(err).isNotNull();
        assertThat(err.getError()).as("error body present").isNotNull();
        assertThat(err.getError().getMessage()).as("error message").isNotBlank();
        logger.info("Groq rejected bad key as expected: {}", err.getError().getMessage());
    }
}
