package com.cybattis.swiftycompanion.auth;

import android.content.Context;
import android.util.Log;

import com.cybattis.swiftycompanion.BuildConfig;
import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiError;
import com.cybattis.swiftycompanion.backend.ErrorUtils;

import retrofit2.Call;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static AuthManager instance = null;
    Api42Service service;
    private String token;

    public static AuthManager getInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new AuthManager(mainActivity.getService());
        }
        return instance;
    }

    public AuthManager(Api42Service _service) {
        service = _service;
        generateToken();
    }

    public void generateToken() {
        Thread thread = new Thread(this::requestToken);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestToken() {
        Call<Tokens> getToken = service.getToken(
                "client_credentials",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET);

        try {
            Response<Tokens> response = getToken.execute();
            if (response.isSuccessful()) {
                Tokens newTokens = response.body();
                if (newTokens != null) {
                    Log.d(TAG, "onResponse: " + newTokens.token);
                    token = newTokens.token;
                    return;
                }
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }
        token = "";
    }

    public boolean tokenInfo() {
        Call<TokenInfo> tokenInfo = service.getTokenInfo("Bearer " + token);
        try {
            Response<TokenInfo> response = tokenInfo.execute();
            if (response.isSuccessful()) {
                TokenInfo info = response.body();
                if (info != null) {
                    return info.isExpired();
                }
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }

        return false;
    }

    public boolean isTokenValid() {
        if (token.isEmpty())
            return false;

        TokenInfoRunnable runnable = new TokenInfoRunnable();
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return !runnable.isExpired();
    }

    public String getToken() {
        return token;
    }

    public class TokenInfoRunnable implements Runnable {
        private volatile boolean expired = false;

        @Override
        public void run() {
            expired = tokenInfo();
        }

        public boolean isExpired() {
            return expired;
        }
    }
}
