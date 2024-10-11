package com.cybattis.swiftycompanion.backend;

public class ApiResponse {

    private int statusCode;
    private String message;

    public ApiResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
