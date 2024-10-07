package com.cybattis.swiftycompanion.auth;

import android.content.Context;
import android.net.Uri;

import com.cybattis.swiftycompanion.BuildConfig;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.io.IOException;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static final Uri API42_AUTHORIZE_URI = Uri.parse("https://api.intra.42.fr/oauth/authorize");
    private static final Uri API42_TOKEN_URI = Uri.parse("https://api.intra.42.fr/oauth/token");
    private static final Uri REDIRECT_URI = Uri.parse("http://www.swifty-companion/redirect");
    public static final String INTENT_AUTHORIZATION_RESPONSE = "com.cybattis.swiftycompanion.AUTHORIZATION_RESPONSE";
    public static final String USED_INTENT = "USED_INTENT";

    private Context context;
    private static AuthManager instance = null;
    private String token = null;

    private AuthManager(Context context) {
        context = context.getApplicationContext();
    }

    public static AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public void generateToken() throws IOException {
        // Generate token
    }

    public boolean isTokenValid() {
        return token != null;
    }

    public void clearToken() {
        token = null;
    }

    public void checkToken() {
        // Check token
        // if token valide go to ProfileFragment
        // else go to LoginFragment
    }

    public static AuthorizationRequest createAuthorizationRequest() {

        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                API42_AUTHORIZE_URI, API42_TOKEN_URI, null
        );

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                BuildConfig.APP_UID,
                ResponseTypeValues.CODE,
                REDIRECT_URI
        );

        return builder.build();
    }

}
