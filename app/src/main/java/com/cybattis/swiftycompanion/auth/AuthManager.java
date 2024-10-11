package com.cybattis.swiftycompanion.auth;

import android.util.Log;
import android.widget.Toast;

import com.cybattis.swiftycompanion.BuildConfig;
import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiResponse;
import com.cybattis.swiftycompanion.backend.ErrorUtils;

import retrofit2.Call;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static AuthManager instance = null;
    Api42Service service;
    MainActivity mainActivity;
    private TokenResponse token;

    public static AuthManager getInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new AuthManager(mainActivity);
        }
        return instance;
    }

    public AuthManager(MainActivity _mainActivity) {
        service = _mainActivity.getService();
        mainActivity = _mainActivity;
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

        if (token.response.status() != 200) {
            Log.d(TAG, "generateToken: " + token.response.message());
            Toast.makeText(mainActivity, "Error: " + token.response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    public void requestToken() {
        Call<Token> getToken = service.getToken(
                "client_credentials",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET);

        try {
            Response<Token> response = getToken.execute();
            ApiResponse apiResponse = ErrorUtils.parseError(response);
            if (response.isSuccessful()) {
                Token newTokens = response.body();
                if (newTokens != null) {
                    token = new TokenResponse(newTokens, apiResponse);
                }
            } else {
                ApiResponse error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
                token = new TokenResponse(new Token(""), error);
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
            token = new TokenResponse(new Token(""), new ApiResponse(500, "Network error"));
        }
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
                ApiResponse error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }

        return false;
    }

    public boolean isTokenValid() {
        if (token.getToken().isEmpty())
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
        return token.getToken();
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
