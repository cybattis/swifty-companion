package com.cybattis.swiftycompanion.auth;

import com.cybattis.swiftycompanion.backend.ApiResponse;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("resource_owner_id")
    public int resourceOwnerId;

    @SerializedName("scopes")
    public String[] scopes;

    @SerializedName("expires_in_seconds")
    public int expiresInSeconds;

    @SerializedName("application")
    public Application application;

    @SerializedName("created_at")
    public long createdAt;

    public static class Application {
        @SerializedName("uid")
        public String uid;
    }

    public ApiResponse response;

    public TokenResponse() {
        resourceOwnerId = 0;
        scopes = new String[0];
        expiresInSeconds = 0;
        application = new Application();
        createdAt = 0;
        response = new ApiResponse();
    }

    public boolean isExpired() {
        return expiresInSeconds == 0;
    }

    public int getStatusCode() {
        return response.status();
    }

    public String getMessage() {
        return response.message();
    }
}
