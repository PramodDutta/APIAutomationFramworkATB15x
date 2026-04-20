package com.thetestingacademy.tests.schema;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.utils.SchemaValidator;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TestSchemaValidation extends BaseTest {

    @Test(groups = "qa", priority = 1)
    @Owner("Promode")
    @Description("TC#SCHEMA1 - Validate booking creation response schema")
    public void testBookingResponseSchema() {
        logger.info("=== TC#SCHEMA1: Testing Booking Response Schema ===");

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Validate using AssertActions helper method
        assertActions.verifyBookingResponseSchema(response);

        logger.info("Booking response schema validation passed");
    }

    @Test(groups = "qa", priority = 2)
    @Owner("Promode")
    @Description("TC#SCHEMA2 - Validate token response schema")
    public void testTokenResponseSchema() {
        logger.info("=== TC#SCHEMA2: Testing Token Response Schema ===");

        requestSpecification.basePath(APIConstants.AUTH_URL);
        String payload = payloadManager.setAuthPayload();

        response = RestAssured.given(requestSpecification)
                .when().body(payload)
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Validate using AssertActions helper method
        assertActions.verifyTokenResponseSchema(response);

        logger.info("Token response schema validation passed");
    }

    @Test(groups = "qa", priority = 3)
    @Owner("Promode")
    @Description("TC#SCHEMA3 - Validate booking GET response schema")
    public void testBookingSchema() {
        logger.info("=== TC#SCHEMA3: Testing Booking Schema (GET by ID) ===");

        // First create a booking to get a valid ID
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        int bookingId = response.jsonPath().getInt("bookingid");
        logger.info("Created booking with ID: {}", bookingId);

        // Now GET the booking by ID
        String getPath = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(getPath);
        response = RestAssured.given(requestSpecification)
                .when().get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Validate booking schema
        assertActions.verifyBookingSchema(response);

        logger.info("Booking schema validation passed for ID: {}", bookingId);
    }

    @Test(groups = "qa", priority = 4)
    @Owner("Promode")
    @Description("TC#SCHEMA4 - Verify invalid schema detection using isValidSchema")
    public void testInvalidSchemaFails() {
        logger.info("=== TC#SCHEMA4: Testing Invalid Schema Detection ===");

        // Create a booking to get a valid response
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Booking response should pass booking-response-schema
        boolean validWithCorrectSchema = SchemaValidator.isValidSchema(response, SchemaValidator.Schemas.BOOKING_RESPONSE);
        assertTrue(validWithCorrectSchema, "Response should match booking-response-schema");

        // Booking response should NOT pass token-response-schema
        boolean validWithWrongSchema = SchemaValidator.isValidSchema(response, SchemaValidator.Schemas.TOKEN_RESPONSE);
        assertFalse(validWithWrongSchema, "Booking response should NOT match token-response-schema");

        logger.info("Invalid schema detection test passed");
    }

    @Test(groups = "qa", priority = 5)
    @Owner("Promode")
    @Description("TC#SCHEMA5 - Validate schema using generic method with custom schema file")
    public void testGenericSchemaValidation() {
        logger.info("=== TC#SCHEMA5: Testing Generic Schema Validation ===");

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Use generic verifyResponseSchema method
        assertActions.verifyResponseSchema(response, "booking-response-schema.json");

        logger.info("Generic schema validation passed");
    }
}
