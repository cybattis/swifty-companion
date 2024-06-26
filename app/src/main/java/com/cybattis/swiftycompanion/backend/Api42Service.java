package com.cybattis.swiftycompanion.backend;

import android.content.Context;

public class Api42Service {
    private static final String TAG = "Api42Service";
    private static Api42Service instance = null;

    private Api42Service(Context context) {
        // Private constructor
    }

    public static Api42Service getInstance(Context context) {
        if (instance == null) {
            instance = new Api42Service(context);
        }
        return instance;
    }

    public void getProfile(String login) {
        // Get profile
    }
}
