package com.cybattis.SwiftyCompanion.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cybattis.SwiftyCompanion.MainActivity;
import com.cybattis.SwiftyCompanion.R;
import com.cybattis.SwiftyCompanion.profile.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Optional;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        TextInputEditText loginInput = view.findViewById(R.id.login_text_input);
        TextInputEditText passwordInput = view.findViewById(R.id.password_text_input);

        view.findViewById(R.id.login_button).setOnClickListener(v ->
            loginButtonCallback(loginInput, passwordInput)
        );
        return view;
    }

    private void loginButtonCallback(TextInputEditText loginInput, TextInputEditText passwordInput) {
        String login = loginInput.getText().toString() != null ? loginInput.getText().toString() : "";
        String password = passwordInput.getText().toString() != null ? passwordInput.getText().toString() : "";

        Log.d("MainActivity", "Empty login or password");
        if (login.isEmpty() || password.isEmpty()) {
            return;
        }

        // Get token

        // if token is valid go to ProfileFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main, new ProfileFragment())
                .commit();

        // else show error message

    }
}