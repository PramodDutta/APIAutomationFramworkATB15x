package com.thetestingacademy.tests.e2e_integration;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.pojos.restfulbook.responsePojos.BookingResponse;
import com.thetestingacademy.utils.MySqlDBConnector;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TestIntegrationFlow4 extends BaseTest {

    // Test E2E Scenario 4 - With MySQL DB Validation
    // 1. Create a Booking via API -> bookingID
    // 2. Validate the Booking exists in MySQL Database
    // 3. Update the Booking via API
    // 4. Verify the Update is reflected in Database

    @Test(groups = "qa", priority = 1)
    @Owner("Promode")
    @Description("TC#INT4 - Step 1. Create a Booking via API")
    public void testCreateBooking(ITestContext iTestContext) {
        logger.info("=== TC#INT4 - Step 1: Creating Booking via API ===");

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava_DeSerialization(response.asString());
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        Integer bookingid = bookingResponse.getBookingid();
        String firstname = bookingResponse.getBooking().getFirstname();
        String lastname = bookingResponse.getBooking().getLastname();

        iTestContext.setAttribute("bookingid", bookingid);
        iTestContext.setAttribute("firstname", firstname);
        iTestContext.setAttribute("lastname", lastname);

        logger.info("Booking created via API with ID: {}, firstname: {}, lastname: {}", bookingid, firstname, lastname);
    }

    @Test(groups = "qa", priority = 2)
    @Owner("Promode")
    @Description("TC#INT4 - Step 2. Validate Booking exists in MySQL Database")
    public void testValidateBookingInDatabase(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String expectedFirstname = (String) iTestContext.getAttribute("firstname");
        String expectedLastname = (String) iTestContext.getAttribute("lastname");

        logger.info("=== TC#INT4 - Step 2: Validating Booking ID: {} in Database ===", bookingid);

        try (Connection connection = MySqlDBConnector.getConnection()) {
            logger.info("MySQL connection established successfully");

            List<Map<String, Object>> rows = MySqlDBConnector.executeSelect(
                    connection,
                    "SELECT booking_id, firstname, lastname FROM booking WHERE booking_id = ?",
                    bookingid
            );

            assertThat(rows)
                    .as("Booking record should exist in database for ID: " + bookingid)
                    .isNotEmpty();

            Map<String, Object> bookingRow = rows.get(0);
            String dbFirstname = (String) bookingRow.get("firstname");
            String dbLastname = (String) bookingRow.get("lastname");

            assertThat(dbFirstname)
                    .as("Firstname in DB should match API response")
                    .isEqualTo(expectedFirstname);

            assertThat(dbLastname)
                    .as("Lastname in DB should match API response")
                    .isEqualTo(expectedLastname);

            logger.info("Database validation successful - firstname: {}, lastname: {}", dbFirstname, dbLastname);

        } catch (Exception e) {
            logger.error("Database validation failed: {}", e.getMessage());
            throw new RuntimeException("Database validation failed for booking ID: " + bookingid, e);
        }
    }

    @Test(groups = "qa", priority = 3)
    @Owner("Promode")
    @Description("TC#INT4 - Step 3. Update Booking via API")
    public void testUpdateBookingViaAPI(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        logger.info("=== TC#INT4 - Step 3: Updating Booking ID: {} via API ===", bookingid);

        String token = getToken();
        iTestContext.setAttribute("token", token);

        String basePathPATCH = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;

        requestSpecification.basePath(basePathPATCH);

        String updatedFirstname = "UpdatedJohn";
        String updatedLastname = "UpdatedDoe";

        response = RestAssured.given(requestSpecification)
                .cookie("token", token)
                .when().body(payloadManager.createPartialUpdatePayload(updatedFirstname, updatedLastname))
                .patch();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        iTestContext.setAttribute("updatedFirstname", updatedFirstname);
        iTestContext.setAttribute("updatedLastname", updatedLastname);

        logger.info("Booking updated via API - firstname: {}, lastname: {}", updatedFirstname, updatedLastname);
    }

    @Test(groups = "qa", priority = 4)
    @Owner("Promode")
    @Description("TC#INT4 - Step 4. Verify Update is reflected in Database")
    public void testVerifyUpdateInDatabase(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String expectedFirstname = (String) iTestContext.getAttribute("updatedFirstname");
        String expectedLastname = (String) iTestContext.getAttribute("updatedLastname");

        logger.info("=== TC#INT4 - Step 4: Verifying Update in Database for Booking ID: {} ===", bookingid);

        try (Connection connection = MySqlDBConnector.getConnection()) {
            logger.info("MySQL connection established successfully");

            List<Map<String, Object>> rows = MySqlDBConnector.executeSelect(
                    connection,
                    "SELECT booking_id, firstname, lastname FROM booking WHERE booking_id = ?",
                    bookingid
            );

            assertThat(rows)
                    .as("Booking record should exist in database for ID: " + bookingid)
                    .isNotEmpty();

            Map<String, Object> bookingRow = rows.get(0);
            String dbFirstname = (String) bookingRow.get("firstname");
            String dbLastname = (String) bookingRow.get("lastname");

            assertThat(dbFirstname)
                    .as("Updated firstname in DB should match")
                    .isEqualTo(expectedFirstname);

            assertThat(dbLastname)
                    .as("Updated lastname in DB should match")
                    .isEqualTo(expectedLastname);

            logger.info("Database update verification successful - firstname: {}, lastname: {}", dbFirstname, dbLastname);

        } catch (Exception e) {
            logger.error("Database verification failed: {}", e.getMessage());
            throw new RuntimeException("Database verification failed for booking ID: " + bookingid, e);
        }
    }
}
