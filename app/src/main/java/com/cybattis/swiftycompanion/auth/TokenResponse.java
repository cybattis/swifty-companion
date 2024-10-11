package com.cybattis.swiftycompanion.auth;

import com.cybattis.swiftycompanion.backend.ApiResponse;

public class TokenResponse
{
    public Token token;
    public ApiResponse response;

    public TokenResponse(Token token, ApiResponse response) {
        this.token = token;
        this.response = response;
    }

    public String getToken() {
        return token.token;
    }

    public int getStatusCode() {
        return response.status();
    }

    public String getMessage() {
        return response.message();
    }
}
