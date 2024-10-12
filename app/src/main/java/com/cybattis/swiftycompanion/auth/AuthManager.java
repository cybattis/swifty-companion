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
    private final Api42Service service;
    private final MainActivity mainActivity;
    private LoginResponse login;

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
        login = new LoginResponse();

        Thread thread = new Thread(this::requestToken);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (login.response.status() != 200) {
            Toast.makeText(mainActivity, login.response.message(), Toast.LENGTH_SHORT).show();
        }
    }

    public void requestToken() {
        Call<LoginResponse> getToken = service.getToken(
                "client_credentials",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET);

        try {
            Response<LoginResponse> response = getToken.execute();
            if (response.isSuccessful()) {
                LoginResponse newLogin = response.body();
                if (newLogin != null) {
                    login.token = newLogin.token;
                    login.response = new ApiResponse(200, "Token generated");
                }
            } else {
                login.response = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + login.response.status() + " " + login.response.message());
            }
        } catch (Exception ex) {
            login.response = new ApiResponse(500, "Network error");
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }
    }

    public boolean tokenInfo() {
        Call<TokenResponse> tokenInfo = service.getTokenInfo("Bearer " + login.getToken());
        try {
            Response<TokenResponse> response = tokenInfo.execute();
            if (response.isSuccessful()) {
                TokenResponse info = response.body();
                if (info != null) {
                    info = response.body();
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
        if (login.getToken().isEmpty())
            return false;

        TokenInfoRunnable runnable = new TokenInfoRunnable();
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (Exception e) {
            Log.e(TAG, "isTokenValid: " + e.getMessage(), e);
        }

        return !runnable.isExpired();
    }

    public String getToken() {
        return login.getToken();
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
