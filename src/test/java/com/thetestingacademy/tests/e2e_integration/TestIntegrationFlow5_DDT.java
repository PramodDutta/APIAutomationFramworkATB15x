package com.thetestingacademy.tests.e2e_integration;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.pojos.restfulbook.requestPOJOs.Booking;
import com.thetestingacademy.pojos.restfulbook.requestPOJOs.Bookingdates;
import com.thetestingacademy.pojos.restfulbook.responsePojos.BookingResponse;
import com.thetestingacademy.utilExcels.UtilExcel;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestIntegrationFlow5_DDT extends BaseTest {

    // Test E2E Scenario 5 - Data-Driven Testing
    // Read test data from Excel file and create bookings dynamically

    @DataProvider(name = "bookingData")
    public Object[][] getBookingData() {
        logger.info("Loading test data from Excel file");
        return UtilExcel.getTestDataFromExcel("Booking");
    }

    @Test(dataProvider = "bookingData", groups = "qa")
    @Owner("Promode")
    @Description("TC#INT5 - DDT: Create Booking with data from Excel")
    public void testCreateBookingFromExcel(String firstname, String lastname, String totalprice,
                                           String depositpaid, String checkin, String checkout,
                                           String additionalneeds) {

        logger.info("=== TC#INT5 - DDT: Creating Booking from Excel Data ===");
        logger.info("Test Data - firstname: {}, lastname: {}, totalprice: {}",
                firstname, lastname, totalprice);

        // Build booking payload from Excel data
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(Integer.parseInt(totalprice.replace(".0", "")));
        booking.setDepositpaid(Boolean.parseBoolean(depositpaid));

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin(checkin);
        bookingdates.setCheckout(checkout);
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds(additionalneeds);

        Gson gson = new Gson();
        String payload = gson.toJson(booking);
        logger.debug("Request Payload: {}", payload);

        // Create booking
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payload)
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Verify response
        BookingResponse bookingResponse = payloadManager.bookingResponseJava_DeSerialization(response.asString());
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), firstname);
        assertActions.verifyStringKey(bookingResponse.getBooking().getLastname(), lastname);

        logger.info("Booking created successfully with ID: {} for {} {}",
                bookingResponse.getBookingid(), firstname, lastname);
    }

    @Test(dataProvider = "bookingData", groups = "qa")
    @Owner("Promode")
    @Description("TC#INT5 - DDT: Create and Verify Booking with data from Excel")
    public void testCreateAndVerifyBookingFromExcel(String firstname, String lastname, String totalprice,
                                                     String depositpaid, String checkin, String checkout,
                                                     String additionalneeds) {

        logger.info("=== TC#INT5 - DDT: Create and Verify Booking from Excel ===");

        // Build booking payload from Excel data
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(Integer.parseInt(totalprice.replace(".0", "")));
        booking.setDepositpaid(Boolean.parseBoolean(depositpaid));

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin(checkin);
        bookingdates.setCheckout(checkout);
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds(additionalneeds);

        Gson gson = new Gson();
        String payload = gson.toJson(booking);

        // Step 1: Create booking
        logger.info("Step 1: Creating booking for {} {}", firstname, lastname);
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
                .when().body(payload)
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava_DeSerialization(response.asString());
        Integer bookingId = bookingResponse.getBookingid();
        logger.info("Booking created with ID: {}", bookingId);

        // Step 2: Verify booking via GET
        logger.info("Step 2: Verifying booking ID: {}", bookingId);
        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        requestSpecification.basePath(basePathGET);

        response = RestAssured.given(requestSpecification)
                .when().get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        Booking getBookingResponse = payloadManager.getResponseFromJSON(response.asString());
        assertActions.verifyStringKey(getBookingResponse.getFirstname(), firstname);
        assertActions.verifyStringKey(getBookingResponse.getLastname(), lastname);

        logger.info("Booking verified successfully for ID: {}", bookingId);
    }
}
