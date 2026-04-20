package com.thetestingacademy.tests.e2e_integration;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.pojos.restfulbook.responsePojos.BookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class TestIntegrationFlow3 extends BaseTest {

    // Test E2E Scenario 3
    // 1. Create a Booking -> bookingID
    // 2. Delete the Booking immediately
    // 3. Verify the Booking is deleted (GET should return 404)

    @Test(groups = "qa", priority = 1)
    @Owner("Promode")
    @Description("TC#INT3 - Step 1. Create a Booking")
    public void testCreateBooking(ITestContext iTestContext) {
        logger.info("=== TC#INT3 - Step 1: Creating Booking ===");

        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payloadManager.createPayloadBookingAsString_Serialization())
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava_DeSerialization(response.asString());
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

        Integer bookingid = bookingResponse.getBookingid();
        iTestContext.setAttribute("bookingid", bookingid);

        logger.info("Booking created successfully with ID: {}", bookingid);
    }

    @Test(groups = "qa", priority = 2)
    @Owner("Promode")
    @Description("TC#INT3 - Step 2. Delete the Booking immediately")
    public void testDeleteBooking(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        logger.info("=== TC#INT3 - Step 2: Deleting Booking ID: {} ===", bookingid);

        String token = getToken();
        iTestContext.setAttribute("token", token);

        String basePathDELETE = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;

        requestSpecification.basePath(basePathDELETE);
        validatableResponse = RestAssured.given(requestSpecification)
                .cookie("token", token)
                .when().delete().then().log().all();

        validatableResponse.statusCode(201);

        logger.info("Booking deleted successfully for ID: {}", bookingid);
    }

    @Test(groups = "qa", priority = 3)
    @Owner("Promode")
    @Description("TC#INT3 - Step 3. Verify the Booking is deleted")
    public void testVerifyBookingDeleted(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        logger.info("=== TC#INT3 - Step 3: Verifying Booking ID: {} is deleted ===", bookingid);

        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;

        requestSpecification.basePath(basePathGET);
        response = RestAssured.given(requestSpecification)
                .when().get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(404);

        logger.info("Verified booking ID: {} is deleted (404 Not Found)", bookingid);
    }
}
