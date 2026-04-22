package com.thetestingacademy.pojos.vwo.responsePojo;


import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class InvalidLoginResponse {

    @SerializedName("message")
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

}