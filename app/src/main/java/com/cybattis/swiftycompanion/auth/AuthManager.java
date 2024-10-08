package com.cybattis.swiftycompanion.auth;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.cybattis.swiftycompanion.BuildConfig;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthManager {
    private static final String TAG = "AuthManager";

    Api42Service service;

    RxDataStore<Preferences> dataStore;
    Preferences.Key<String> TOKEN = PreferencesKeys.stringKey("token");
    Preferences.Key<String> REFRESH_TOKEN = PreferencesKeys.stringKey("token");

    private Tokens tokens;

    public AuthManager(Context context) {
        context = context.getApplicationContext();
        service = Api42Client.getClient().create(Api42Service.class);
        dataStore = new RxPreferenceDataStoreBuilder(context, /*name=*/ "credentials").build();
        tokens = getStoredToken();
    }

    public Tokens getStoredToken() {
        try {
            Flowable<String> tokenData = dataStore.data().map(prefs -> prefs.get(TOKEN));
            Flowable<String> refreshTokenData = dataStore.data().map(prefs -> prefs.get(REFRESH_TOKEN));

            Log.d(TAG, "getToken: " + tokenData.blockingFirst());
            Log.d(TAG, "getToken: " + refreshTokenData.blockingFirst());

            return new Tokens(tokenData.blockingFirst(), refreshTokenData.blockingFirst());
        } catch (Exception ex) {
            Log.w(TAG, "getToken: no token");
            Log.d(TAG, "getToken: ", ex);
            return new Tokens("", "");
        }
    }

    public boolean generateToken(String code) {

        Call<Tokens> getToken = service.getToken(
                "authorization_code",
                BuildConfig.APP_UID,
                BuildConfig.APP_SECRET,
                code,
                BuildConfig.REDIRECT_URL,
                BuildConfig.AUTH_URL_STATE);
        getToken.enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(@NonNull Call<Tokens> call, @NonNull Response<Tokens> response) {
                Tokens newTokens = response.body();
                if (newTokens != null) {
                    Log.d(TAG, "onResponse: " + newTokens.token);
                    Log.d(TAG, "onResponse: " + newTokens.refresh_token);

                    tokens.token = newTokens.token;
                    tokens.refresh_token = newTokens.refresh_token;

                    // Store new token
                    dataStore.updateDataAsync(prefsIn -> {
                        MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                        mutablePreferences.set(TOKEN, tokens.token);
                        mutablePreferences.set(REFRESH_TOKEN, tokens.refresh_token);
                        return Single.just(mutablePreferences);
                    });
                }
            }
            @Override
            public void onFailure(@NonNull Call<Tokens> call, @NonNull Throwable throwable) {
                call.cancel();
            }
        });

        Log.d(TAG, "generateToken: " + getToken.isCanceled());
        return !getToken.isCanceled();
    }

    public boolean isTokenValid() {
        if (tokens.token == null || tokens.token.isEmpty())
            return false;
        return true;
    }

    public Tokens getToken() {
        return tokens;
    }
}
