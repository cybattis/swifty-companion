package com.cybattis.swiftycompanion;

import static com.cybattis.swiftycompanion.auth.AuthManager.INTENT_AUTHORIZATION_RESPONSE;
import static com.cybattis.swiftycompanion.auth.AuthManager.USED_INTENT;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.profile.ProfileFragment;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    AuthorizationService mAuthorizationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuthorizationService = new AuthorizationService(this);

        findViewById(R.id.login_button).setOnClickListener(v -> doAuth());
    }

    private void doAuth() {
        AuthorizationRequest authorizationRequest = AuthManager.createAuthorizationRequest();

        PendingIntent authorizationIntent = PendingIntent.getActivity(
                this,
                authorizationRequest.hashCode(),
                new Intent(INTENT_AUTHORIZATION_RESPONSE, null, this, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);

        /* request sample with custom tabs */
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        mAuthorizationService.performAuthorizationRequest(authorizationRequest, authorizationIntent, customTabsIntent);
    }

    private void loginButtonCallback() {
        // if token is valid go to ProfileFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();
        // else show error message
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Entering onStart ...");
        super.onStart();
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "Entering onNewIntent ...");
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        Log.d(TAG, "Entering checkIntent ...");
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) {
                Log.w(TAG, "checkIntent action is null!");
                return;
            }
            switch (action) {
                case INTENT_AUTHORIZATION_RESPONSE:
                    Log.d(TAG, "checkIntent action = " + action
                            + " intent.hasExtra(USED_INTENT) = " + intent.hasExtra(USED_INTENT));
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    Log.w(TAG, "checkIntent action = " + action);
                    // do nothing
            }
        } else {
            Log.w(TAG, "checkIntent intent is null!");
        }
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {

        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);

        if (response == null) {
            Log.w(TAG, "Authorization Response is null ");
            Log.d(TAG, "Authorization Exception = " + error);
            return;
        }

        Log.i(TAG, "Entering handleAuthorizationResponse with response from Intent = " + response.jsonSerialize());

        if (response.authorizationCode != null ) { // Authorization Code method: succeeded to get code

            final AuthState authState = new AuthState(response, error);
            Log.i(TAG, "Received code = " + response.authorizationCode + "\n make another call to get token ...");

            // File 2nd call in Authorization Code method to get the token
            mAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (tokenResponse != null) {
                        authState.update(tokenResponse, exception);
//                            mAuthStateDAL.writeAuthState(authState); //store into persistent storage for use later
                        String text = String.format("Received token response [%s]", tokenResponse.jsonSerializeString());
                        Log.i(TAG, text);
//                            accessToken = tokenResponse.accessToken;
//                            expiresAt = tokenResponse.accessTokenExpirationTime.toString();
//                            refreshToken = tokenResponse.refreshToken;
//                            showAuthInfo();
                    } else {
                        Context context = getApplicationContext();
                        Log.w(TAG, "Token Exchange failed", exception);
                        CharSequence text = "Token Exchange failed";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });
        } else { // come here w/o authorization code. For example, signup finish and user clicks "Back to login"
            Log.d(TAG, "additionalParameter = " + response.additionalParameters.toString());
            if (response.additionalParameters.get("status").equalsIgnoreCase("login_request")) {
                doAuth();
                return;
            }
            Log.d(TAG, response.jsonSerialize().toString());
        }
    }
}