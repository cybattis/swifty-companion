package com.cybattis.swiftycompanion.profile;

import com.google.gson.annotations.SerializedName;

public class Users {

    @SerializedName("id")
    public String id;

    public Users(String id) {
        this.id = id;
    }
}
