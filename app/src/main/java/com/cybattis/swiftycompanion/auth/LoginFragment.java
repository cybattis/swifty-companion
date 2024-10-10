package com.cybattis.swiftycompanion.auth;

import static android.app.Activity.RESULT_CANCELED;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.R;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginActivity";
    private AuthManager authManager;

    ActivityResultLauncher<Intent> webViewLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) {
                        Log.d(TAG, "onActivityResult: no data");
                        Toast.makeText(getContext(), "Failed to login to 42", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String code = data.getStringExtra("code");
                    getToken(code);
                }
                if (result.getResultCode() == RESULT_CANCELED) {
                    // Failed auth
                    Log.d(TAG, "onActivityResult: login to 42 failed");
                }
            });

    public LoginFragment() {
        // Required empty public constructor
    }

    private void getToken(String code) {
        Thread thread = new Thread(() -> {
            authManager.generateToken(code);
        });
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authManager = AuthManager.getInstance((MainActivity)requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        view.findViewById(R.id.login_button).setOnClickListener(v -> doAuth());
        return view;
    }

    private void doAuth() {
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        webViewLauncher.launch(intent);
    }
}