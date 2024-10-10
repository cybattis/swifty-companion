package com.cybattis.swiftycompanion.auth;

import com.google.gson.annotations.SerializedName;

public class TokenInfo {
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

    public TokenInfo(int resourceOwnerId, String[] scopes, int expiresInSeconds, Application application, long createdAt) {
        this.resourceOwnerId = resourceOwnerId;
        this.scopes = scopes;
        this.expiresInSeconds = expiresInSeconds;
        this.application = application;
        this.createdAt = createdAt;
    }

    public boolean isExpired() {
        return expiresInSeconds == 0;
    }
}
