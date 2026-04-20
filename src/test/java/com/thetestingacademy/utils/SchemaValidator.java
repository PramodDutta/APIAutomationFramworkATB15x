package com.thetestingacademy.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public class SchemaValidator {

    private static final Logger logger = LogManager.getLogger(SchemaValidator.class);
    private static final String SCHEMA_PATH = "schemas/";

    public static void validateSchema(Response response, String schemaFileName) {
        String schemaPath = SCHEMA_PATH + schemaFileName;
        logger.info("Validating response against schema: {}", schemaPath);

        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));

        logger.info("Schema validation passed for: {}", schemaFileName);
    }

    public static void validateSchema(String jsonString, String schemaFileName) {
        String schemaPath = SCHEMA_PATH + schemaFileName;
        logger.info("Validating JSON string against schema: {}", schemaPath);

        assertThat(jsonString, matchesJsonSchemaInClasspath(schemaPath));

        logger.info("Schema validation passed for: {}", schemaFileName);
    }

    public static boolean isValidSchema(Response response, String schemaFileName) {
        String schemaPath = SCHEMA_PATH + schemaFileName;
        try {
            response.then().assertThat()
                    .body(matchesJsonSchemaInClasspath(schemaPath));
            logger.info("Schema validation passed for: {}", schemaFileName);
            return true;
        } catch (AssertionError e) {
            logger.warn("Schema validation failed for {}: {}", schemaFileName, e.getMessage());
            return false;
        }
    }

    public static boolean isValidSchema(String jsonString, String schemaFileName) {
        String schemaPath = SCHEMA_PATH + schemaFileName;
        try {
            assertThat(jsonString, matchesJsonSchemaInClasspath(schemaPath));
            logger.info("Schema validation passed for: {}", schemaFileName);
            return true;
        } catch (AssertionError e) {
            logger.warn("Schema validation failed for {}: {}", schemaFileName, e.getMessage());
            return false;
        }
    }

    public static class Schemas {
        public static final String BOOKING = "booking-schema.json";
        public static final String BOOKING_RESPONSE = "booking-response-schema.json";
        public static final String TOKEN_RESPONSE = "token-response-schema.json";
        public static final String ERROR_RESPONSE = "error-response-schema.json";
    }
}
