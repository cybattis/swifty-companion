package com.cybattis.swiftycompanion;

import static com.cybattis.swiftycompanion.auth.AuthManager.INTENT_AUTHORIZATION_RESPONSE;
import static com.cybattis.swiftycompanion.auth.AuthManager.USED_INTENT;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.WebViewActivity;
import com.cybattis.swiftycompanion.profile.ProfileFragment;

import net.openid.appauth.AuthorizationService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    AuthorizationService mAuthorizationService;

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> webviewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    Log.d(TAG, "onActivityResult: " + data.getStringExtra("code"));
                    // Get token
                    loginButtonCallback();
                }
            });

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
        Intent intent = new Intent(this,
                WebViewActivity.class);
        webviewLauncher.launch(intent);
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
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "Entering onNewIntent ...");
    }
}