package com.thetestingacademy.base;

import com.thetestingacademy.asserts.AssertActions;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.modules.PayloadManager;
import com.thetestingacademy.modules.vwo.VWOPayloadManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    // This is common to all test cases.
    public RequestSpecification requestSpecification;
    public Response response;
    public ValidatableResponse validatableResponse;

    public AssertActions assertActions;
    public PayloadManager payloadManager;
    public VWOPayloadManager vwoPayloadManager;
    public JsonPath jsonPath;

    @BeforeClass
    public void setup() {
        logger.info("=== Starting Test Setup ===");
        logger.info("Initializing PayloadManager, AssertActions, and VWOPayloadManager");

        payloadManager = new PayloadManager();
        assertActions = new AssertActions();
        vwoPayloadManager = new VWOPayloadManager();

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(APIConstants.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .build().log().all();

        logger.info("Base URL set to: {}", APIConstants.BASE_URL);
        logger.info("=== Test Setup Complete ===");
    }

    @AfterClass
    public void tearDown() {
        logger.info("=== Test Teardown Complete ===");
    }

    public String getToken() {
        logger.info("Generating authentication token");
        requestSpecification = RestAssured.given();
        requestSpecification.baseUri(APIConstants.BASE_URL)
                .basePath(APIConstants.AUTH_URL);

        String payload = payloadManager.setAuthPayload();
        logger.debug("Auth payload: {}", payload);

        response = requestSpecification.contentType(ContentType.JSON).body(payload).when().post();
        String token = payloadManager.getTokenFromJSON(response.asString());

        logger.info("Token generated successfully");
        return token;
    }

}
