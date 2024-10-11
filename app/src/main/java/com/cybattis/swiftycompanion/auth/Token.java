package com.cybattis.swiftycompanion.auth;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("access_token")
    public String token;

    public Token(String token) {
        this.token = token;
    }
}
