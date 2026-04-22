package com.thetestingacademy.modules.vwo;

import com.google.gson.Gson;
import com.thetestingacademy.pojos.vwo.requestPOJO.VWOLoginRequest;
import com.thetestingacademy.pojos.vwo.responsePojo.InvalidLoginResponse;

public class VWOPayloadManager {

    Gson gson;

    public String setLoginDataInValid(){
        VWOLoginRequest loginRequest = new VWOLoginRequest();
        loginRequest.setUsername("contact+aug@thetestingacademy.com");
        loginRequest.setPassword("TtxkgQ!s$rJBk85");
        loginRequest.setRemember(false);
        loginRequest.setRecaptchaResponseField("");


        gson = new Gson();
        String jsonPayloadString = gson.toJson(loginRequest);
        System.out.println("Payload Login to the -> " + jsonPayloadString);
        return jsonPayloadString;
    }

    // DeSer ( JSON String -> Java Object
    public String getLoginDataInvalid(String loginResponseEx){
        gson = new Gson();
        InvalidLoginResponse loginResponse = gson.fromJson(loginResponseEx, InvalidLoginResponse.class);
        return  loginResponse.getMessage();

    }


}
