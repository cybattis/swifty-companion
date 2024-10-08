package com.cybattis.swiftycompanion.auth;

import com.google.gson.annotations.SerializedName;

public class Tokens {
    @SerializedName("access_token")
    public String token;
    @SerializedName("refresh_token")
    public String refresh_token;

    public Tokens(String token, String refresh_token) {
        this.token = token;
        this.refresh_token = refresh_token;
    }
}
