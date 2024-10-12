package com.cybattis.swiftycompanion;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiResponse;
import com.cybattis.swiftycompanion.backend.ErrorUtils;
import com.cybattis.swiftycompanion.profile.ProfileFragment;
import com.cybattis.swiftycompanion.profile.User;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AuthManager authManager;
    private FragmentManager fragmentManager;
    private Api42Service service;
    private User userID;

    FrameLayout mainLayout;
    EditText login;
    String loginText;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        service = Api42Client.createService();
        authManager = AuthManager.getInstance(this);
        fragmentManager = getSupportFragmentManager();

        login = findViewById(R.id.input_login);
        mainLayout = findViewById(R.id.main);
        findViewById(R.id.ok_button).setOnClickListener(v -> navigateToProfile());
    }

    public void navigateToProfile() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

        loginText = login.getText().toString();
        if (loginText.isEmpty()) {
            Toast.makeText(this, "Please enter a login", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!getUsers()) {
            Toast.makeText(this, userID.response.message(), Toast.LENGTH_SHORT).show();
            return;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();
    }
    public Api42Service getService() {
        return service;
    }

    public String getUserId() {
        return userID.getId();
    }

    private boolean getUsers() {
        userID = new User();

        Thread thread = new Thread(this::requestUserID);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return userID.response.status() == 200;
    }

    private void requestUserID() {
        if (!authManager.isTokenValid()) {
            authManager.requestToken();
        }

        Call<User[]> getUsers = service.getUsers("Bearer " + authManager.getToken(), loginText);
        try {
            Response<User[]> response = getUsers.execute();
            if (response.isSuccessful()) {
               User[] data = response.body();
                if (data != null) {
                    if (data.length == 0) {
                        userID.response = new ApiResponse(404, "User not found");
                    } else {
                        userID = data[0];
                        userID.response = new ApiResponse(200, "OK");
                    }
                }
            } else {
                ApiResponse error = ErrorUtils.parseError(response);
                Log.e(TAG, "requestUserID: API error: " + error.toString());
                userID.response = ErrorUtils.parseError(response);
            }
        } catch (Exception ex) {
            Log.e(TAG, "requestUserID: " + ex.getMessage());
            userID.response = new ApiResponse(500, "Network error");
        }
    }
}