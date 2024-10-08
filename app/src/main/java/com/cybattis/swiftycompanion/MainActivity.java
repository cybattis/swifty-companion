package com.cybattis.swiftycompanion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.auth.WebViewActivity;
import com.cybattis.swiftycompanion.profile.ProfileFragment;

import net.openid.appauth.AuthorizationService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AuthManager authManager;

    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> webViewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) {
                        Log.d(TAG, "onActivityResult: no data");
                        Toast.makeText(this, "Failed to login to 42", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String code = data.getStringExtra("code");
                    if (authManager.generateToken(code))
                        navigateToProfile();
                    else
                        Toast.makeText(this, "Failed to login to 42", Toast.LENGTH_SHORT).show();
                }
                if (result.getResultCode() == RESULT_CANCELED) {
                    // Failed auth
                    Log.d(TAG, "onActivityResult: login to 42 failed");
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

        findViewById(R.id.login_button).setOnClickListener(v -> doAuth());

        authManager = new AuthManager(getApplicationContext());
    }

    private void doAuth() {
        Intent intent = new Intent(this,
                WebViewActivity.class);
        webViewLauncher.launch(intent);
    }

    private void navigateToProfile() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Entering onStart ...");
        super.onStart();

        if (authManager.isTokenValid())
            navigateToProfile();
    }
}