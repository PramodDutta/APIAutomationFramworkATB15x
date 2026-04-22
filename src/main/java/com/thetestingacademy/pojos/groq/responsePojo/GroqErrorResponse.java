package com.thetestingacademy.pojos.groq.responsePojo;

public class GroqErrorResponse {

    private ErrorBody error;

    public ErrorBody getError() { return error; }

    public static class ErrorBody {
        private String message;
        private String type;
        private String code;

        public String getMessage() { return message; }
        public String getType() { return type; }
        public String getCode() { return code; }
    }
}
