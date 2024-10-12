package com.cybattis.swiftycompanion.profile;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cybattis.swiftycompanion.MainActivity;
import com.cybattis.swiftycompanion.R;
import com.cybattis.swiftycompanion.auth.AuthManager;
import com.cybattis.swiftycompanion.backend.Api42Client;
import com.cybattis.swiftycompanion.backend.Api42Service;
import com.cybattis.swiftycompanion.backend.ApiResponse;
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

        if (activity == null || activity.getUserId() == null) {
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return;
        }

        userID = activity.getUserId();
        getUserData();
        if (user.response.status() != 200) {
            Toast.makeText(requireActivity(), user.response.message(), Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            return;
        }

        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mViewModel.setUserData(user);
        mViewModel.getUserData().observe(this, data -> {
            Log.d(TAG, "onCreate: " + user.toString());

        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(ProfileFragment.this).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView wallet = view.findViewById(R.id.wallet_text);
        wallet.setText(user.wallet + "₳");

        TextView eval_points = view.findViewById(R.id.evalpoint_text);
        eval_points.setText(String.valueOf(user.correctionPoint));

        TextView grade = view.findViewById(R.id.grade_text);
        grade.setText(user.cursusUsers.get(1).grade);

        TextView level = view.findViewById(R.id.cursus_text);
        level.setText(String.valueOf(user.getCursusName()));

        TextView login = view.findViewById(R.id.login_text);
        login.setText(user.login);

        TextView real_name = view.findViewById(R.id.realname_text);
        real_name.setText(user.getFullName());

        TextView text_xp = view.findViewById(R.id.level_text);
        text_xp.setText(user.getXpString());

        ProgressBar progressBar = view.findViewById(R.id.xp_progress);
        progressBar.setProgress(user.getDecimalXp());

        ImageView user_picture = view.findViewById(R.id.user_picture);
        Glide.with(this)
                .load(user.getImage())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.baseline_account_circle_24) // Placeholder image
                        .error(R.drawable.ic_launcher_background) // Error image in case of loading failure
                )
                .centerCrop()
                .into(user_picture);

        RecyclerView recyclerView = view.findViewById(R.id.projects_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProjectAdapter(user.getProjectsList(), getContext()));

        RecyclerView recyclerView2 = view.findViewById(R.id.skills_view);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setAdapter(new SkillAdapter(user.getSkillsList(), getContext()));

        return view;
    }

    private void getUserData() {
        user = new User();

        Thread thread = new Thread(this::requestUserData);
        thread.start();
        try {
            thread.join();
            Log.d(TAG, "Thread ended: " + thread.getState());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestUserData() {
        if (!authManager.isTokenValid()) {
            authManager.requestToken();
        }
        Call<User> getUser = service.getUserData(userID, "Bearer " + authManager.getToken());
        try {
            Response<User> response = getUser.execute();
            if (response.isSuccessful()) {
                user = response.body();
                user.response = new ApiResponse(200, "OK");
            } else {
                ApiResponse error = ErrorUtils.parseError(response);
                Log.d(TAG, "getUserDataData: " + error.toString());
                user.response = ErrorUtils.parseError(response);
            }
        } catch (Exception ex) {
            Log.d(TAG, "getUserDataData: " + ex.getMessage());
            user.response = new ApiResponse(500, "Network error");
        }
    }
}