package com.cybattis.swiftycompanion.auth;

import com.google.gson.annotations.SerializedName;

public class Tokens {
    @SerializedName("access_token")
    public String token;

    public Tokens(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
