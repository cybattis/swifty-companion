package com.cybattis.swiftycompanion;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.auth.LoginFragment;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AuthManager authManager;
    private FragmentManager fragmentManager;
    private Api42Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        service = Api42Client.createService();
        authManager = AuthManager.getInstance(this);
        fragmentManager = getSupportFragmentManager();
    }

    public void navigateToProfile() {
        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();
    }

    public void navigateToLogin() {
        fragmentManager.beginTransaction()
                .replace(R.id.main, new LoginFragment())
                .commit();
    }

    public Api42Service getService() {
        return service;
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Entering onStart ...");
        super.onStart();

        if (authManager.isTokenValid())
            navigateToProfile();
        else
            navigateToLogin();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Entering onResume ...");
        super.onResume();
        if (authManager.isTokenValid())
            navigateToProfile();
        else
            navigateToLogin();
    }
}