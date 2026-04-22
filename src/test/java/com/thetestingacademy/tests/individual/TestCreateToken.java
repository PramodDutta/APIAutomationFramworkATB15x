package com.thetestingacademy.tests.individual;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import io.qameta.allure.Description;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

public class TestCreateToken extends BaseTest {

    @Link("https://bugz.atlassian.net/browse/RBT-7")
    @Description("Verify that post request to the create token basically creates a 16-digit token. ")
    @Owner("Pramod Dutta")
    @Test(groups = "reg", priority = 1)
    public void test_verifyTokenPOST() {


        requestSpecification.basePath(APIConstants.AUTH_URL);
        response = RestAssured.given(requestSpecification).when().body(payloadManager.setAuthPayload()).post();

        // Extraction ( JSON String response to Java Object)
        String token = payloadManager.getTokenFromJSON(response.asString());
        System.out.println(token);


        // Validation of the request.
        assertActions.verifyStringKeyNotNull(token);









    }

    @Test(groups = "reg", priority = 1)
    @TmsLink("https://bugz.atlassian.net/browse/BUG-19")
    @Owner("Promode")
    @Description("TC#2  - Create Invalid Token and Verify")
    public void testTokenPOST_Negative() {
        requestSpecification.basePath(APIConstants.AUTH_URL);
        response = RestAssured.given(requestSpecification)
                .when()
                .body("{}").post();

        String invalid_reason = payloadManager.getInvalidResponse(response.asString());
        System.out.println(invalid_reason);


        // Validation of the request.
        assertActions.verifyStringKey(invalid_reason,"Bad credentials");
    }
}
