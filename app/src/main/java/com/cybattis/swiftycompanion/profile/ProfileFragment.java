package com.cybattis.swiftycompanion.profile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.R;
import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiError;
import com.cybattis.swiftycompanion.backend.ErrorUtils;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private ProfileViewModel mViewModel;
    private Api42Service service;
    private AuthManager authManager;
    private String userID;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = Api42Client.createService();
        authManager = AuthManager.getInstance((MainActivity)requireActivity());

        MainActivity activity = (MainActivity)getActivity();
        userID = activity.getUserId();
        getUserData();

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mViewModel.setUserData(user);
        mViewModel.getUserData().observe(this, user -> {
            Log.d(TAG, "onCreate: " + user.toString());
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView wallet = view.findViewById(R.id.wallet_text);
        wallet.setText(String.valueOf(user.wallet));

        TextView eval_points = view.findViewById(R.id.evalpoint_text);
        eval_points.setText(String.valueOf(user.correctionPoint));

        TextView grade = view.findViewById(R.id.grade_text);
        grade.setText(user.cursusUsers.get(0).grade);

        TextView level = view.findViewById(R.id.xp_text);
        level.setText(String.valueOf(user.getLevel()));

        return view;
    }

    private void setUser(User user) {
        this.user = user;
    }

    private void getUserData() {
        if (!authManager.isTokenValid()) {
            authManager.generateToken();
        }

        Thread thread = new Thread(() -> {
            setUser(requestUserData());
        });
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private User requestUserData() {
        Call<User> getMe = service.getMe(userID, "Bearer " + authManager.getToken());
        try {
            Response<User> response = getMe.execute();
            if (response.isSuccessful()) {
                User data = response.body();
                Log.d(TAG, "getMeData: " + data.toString());
                return data;
            } else {
                ApiError error = ErrorUtils.parseError(response);
                Log.d(TAG, "getMeData: " + error.toString());
            }
        } catch (Exception ex) {
            Log.d(TAG, "getMeData: " + ex.getMessage());
        }
        return null;
    }
}