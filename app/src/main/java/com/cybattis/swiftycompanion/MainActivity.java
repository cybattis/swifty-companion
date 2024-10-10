package com.cybattis.swiftycompanion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiError;
import com.cybattis.swiftycompanion.backend.ErrorUtils;
import com.cybattis.swiftycompanion.profile.ProfileFragment;
import com.cybattis.swiftycompanion.profile.Users;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AuthManager authManager;
    private FragmentManager fragmentManager;
    private Api42Service service;
    private Users[] users;

    EditText login;
    String loginText;

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

        login = findViewById(R.id.input_login);
        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loginText = charSequence.toString();
                getUsers();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        findViewById(R.id.ok_button).setOnClickListener(v -> navigateToProfile());
    }

    public void navigateToProfile() {
        loginText = login.getText().toString();
        if (loginText.isEmpty()) {
            Toast.makeText(this, "Please enter a login", Toast.LENGTH_SHORT).show();
            return;
        }

        getUsers();

        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();
    }

    public Api42Service getService() {
        return service;
    }

    public String getUserId() {
        return users[0].id;
    }

    private void getUsers() {
        Thread thread = new Thread(() -> {
            authManager.generateToken();
            requestUsersList();
        });
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestUsersList() {
        Call<Users[]> getUsers = service.getUsers("Bearer " + authManager.getToken(), loginText);
        try {
            Response<Users[]> response = getUsers.execute();
            if (response.isSuccessful()) {
                Users[] data = response.body();
                users = data;
                Log.d(TAG, "requestUsersList: " + Arrays.toString(data));
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.e(TAG, "requestUsersList: API error: " + error.toString());
            }
        } catch (Exception ex) {
            Log.e(TAG, "requestUsersList: " + ex.getMessage());
        }
    }
}