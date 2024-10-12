package com.cybattis.swiftycompanion.auth;

import com.cybattis.swiftycompanion.backend.ApiResponse;
import com.google.gson.annotations.SerializedName;

public class LoginResponse
{
    @SerializedName("access_token")
    public String token;

    public ApiResponse response;

    public LoginResponse() {
        token = "";
        response = new ApiResponse();
    }

    public String getToken() {
        return token;
    }

    public int getStatusCode() {
        return response.status();
    }

    public String getMessage() {
        return response.message();
    }
}
