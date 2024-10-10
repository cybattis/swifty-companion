package com.cybattis.swiftycompanion.backend;

public class ApiError {

    private int statusCode;
    private String message;

    public ApiError() {
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }
}
