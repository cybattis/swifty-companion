package com.cybattis.swiftycompanion.auth;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.cybattis.swiftycompanion.BuildConfig;
import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiError;
import com.cybattis.swiftycompanion.backend.ErrorUtils;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static AuthManager instance = null;

    Api42Service service;

    RxDataStore<Preferences> dataStore;
    Preferences.Key<String> TOKEN = PreferencesKeys.stringKey("token");
    Preferences.Key<String> REFRESH_TOKEN = PreferencesKeys.stringKey("refresh_token");
    
    private final Tokens tokens;

    public static AuthManager getInstance(MainActivity mainActivity) {
        if (instance == null) {
            instance = new AuthManager(mainActivity.getBaseContext(), mainActivity.getService());
        }
        return instance;
    }

    public AuthManager(Context context, Api42Service _service) {
        dataStore = new RxPreferenceDataStoreBuilder(context, /*name=*/ "credentials").build();
        tokens = getStoredToken();
        service = _service;
    }

    public Tokens getStoredToken() {
        try {
            Flowable<String> tokenData = dataStore.data().map(prefs -> prefs.get(TOKEN));
            Flowable<String> refreshTokenData = dataStore.data().map(prefs -> prefs.get(REFRESH_TOKEN));
            return new Tokens(tokenData.blockingFirst(), refreshTokenData.blockingFirst());
        } catch (Exception ex) {
            Log.w(TAG, "getToken: ", ex);
            return new Tokens("", "");
        }
    }

    public void generateToken(String code) {
        Call<Tokens> getToken = service.getToken(
                "authorization_code",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET,
                code,
                BuildConfig.REDIRECT_URL,
                BuildConfig.AUTH_URL_STATE);

        try {
            Response<Tokens> response = getToken.execute();
            if (response.isSuccessful()) {
                Tokens newTokens = response.body();
                if (newTokens != null) {
                    Log.d(TAG, "onResponse: " + newTokens.token);
                    Log.d(TAG, "onResponse: " + newTokens.refresh_token);

                    tokens.token = newTokens.token;
                    tokens.refresh_token = newTokens.refresh_token;

                    // Store new token
                    Single<Preferences> pref = dataStore.updateDataAsync(prefsIn -> {
                        MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                        mutablePreferences.set(TOKEN, tokens.token);
                        mutablePreferences.set(REFRESH_TOKEN, tokens.refresh_token);
                        return Single.just(mutablePreferences);
                    });

                    pref.doOnError(e -> {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }).subscribe();

                    pref.doOnSuccess(e -> {
                        Log.d(TAG, "Tokens successfully saved");
                    }).subscribe();
                }
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }
    }

    public void refreshToken() {
        Call<Tokens> refreshToken = service.refreshToken(
                "refresh_token",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET,
                tokens.refresh_token);

        try {
            Response<Tokens> response = refreshToken.execute();
            if (response.isSuccessful()) {
                Tokens newTokens = response.body();
                if (newTokens != null) {
                    Log.d(TAG, "onResponse: " + newTokens.token);
                    Log.d(TAG, "onResponse: " + newTokens.refresh_token);

                    tokens.token = newTokens.token;
                    tokens.refresh_token = newTokens.refresh_token;

                    // Store new token
                    Single<Preferences> pref = dataStore.updateDataAsync(prefsIn -> {
                        MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                        mutablePreferences.set(TOKEN, tokens.token);
                        mutablePreferences.set(REFRESH_TOKEN, tokens.refresh_token);
                        return Single.just(mutablePreferences);
                    });

                    pref.doOnError(e -> {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }).subscribe();

                    pref.doOnSuccess(e -> {
                        Log.d(TAG, "Tokens successfully saved");
                    }).subscribe();
                }
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.d(TAG, "onResponse: " + error.status() + " " + error.message());
            }
        } catch (Exception ex) {
            Log.d(TAG, "onFailure: " + ex.getMessage());
        }
    }

    public boolean tokenInfo() {
        Call<TokenInfo> tokenInfo = service.getTokenInfo("Bearer " + tokens.token);
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
        if (tokens.token.isEmpty() || tokens.refresh_token.isEmpty())
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
        return tokens.token;
    }

    public void clearToken() {
        tokens.token = "";
        tokens.refresh_token = "";

        Single<Preferences> pref = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.remove(TOKEN);
            mutablePreferences.remove(REFRESH_TOKEN);
            return Single.just(mutablePreferences);
        });

        pref.doOnError(e -> {
            Log.e(TAG, "Error: " + e.getMessage());
        }).subscribe();

        pref.doOnSuccess(e -> {
            Log.d(TAG, "Tokens successfully removed");
        }).subscribe();
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
