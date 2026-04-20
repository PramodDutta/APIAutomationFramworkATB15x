package com.thetestingacademy.asserts;

import com.thetestingacademy.utils.SchemaValidator;
import io.restassured.response.Response;

import static org.testng.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;
import static org.testng.Assert.assertTrue;

public class AssertActions {

    public void verifyResponseBody(String actual, String expected, String description){
        assertEquals(actual,expected,description);
    }
    public void verifyResponseBody(int actual, int expected, String description) {
        assertEquals(actual, expected, description);
    }
    public void verifyStatusCode(Response response, int expected) {
        assertEquals(response.getStatusCode(),expected);
    }
    public void verifyStringKey(String keyExpect,String keyActual){
        // AssertJ
        assertThat(keyExpect).isNotNull();
        assertThat(keyExpect).isNotBlank();
        assertThat(keyExpect).isEqualTo(keyActual);

    }
    public void verifyStringKeyNotNull(Integer keyExpect){
        // AssertJ
        assertThat(keyExpect).isNotNull();
    }
    public void verifyStringKeyNotNull(String keyExpect){
        // AssertJ
        assertThat(keyExpect).isNotNull();
    }
    public void verifyTrue(boolean keyExpect){
        // Test NG
        assertTrue(keyExpect);
    }

    // Schema Validation Methods
    public void verifyResponseSchema(Response response, String schemaFile) {
        SchemaValidator.validateSchema(response, schemaFile);
    }

    public void verifyBookingResponseSchema(Response response) {
        SchemaValidator.validateSchema(response, SchemaValidator.Schemas.BOOKING_RESPONSE);
    }

    public void verifyBookingSchema(Response response) {
        SchemaValidator.validateSchema(response, SchemaValidator.Schemas.BOOKING);
    }

    public void verifyTokenResponseSchema(Response response) {
        SchemaValidator.validateSchema(response, SchemaValidator.Schemas.TOKEN_RESPONSE);
    }

    public void verifyErrorResponseSchema(Response response) {
        SchemaValidator.validateSchema(response, SchemaValidator.Schemas.ERROR_RESPONSE);
    }
}
