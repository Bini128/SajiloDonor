package com.example.donorblood.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.donorblood.R;

public class Profile extends Fragment {

    private TextView tvName, tvemail, tvLocation;
    private ImageView profileImageView;

    private LinearLayout btnEditProfile, btnRequestBlood, btnChangeLocation, btnDonateRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvName = view.findViewById(R.id.tvName);
        tvemail = view.findViewById(R.id.tvemail);
        tvLocation = view.findViewById(R.id.tvLocation);
        profileImageView = view.findViewById(R.id.profileImageView);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnRequestBlood = view.findViewById(R.id.btnRequestBlood);
        btnChangeLocation = view.findViewById(R.id.btnChangeLocation);
        btnDonateRequest = view.findViewById(R.id.btnDonateRequest);

        // Example static data (replace with real data from database)
        tvName.setText("Binita Lamichhane");
        tvemail.setText("binita@example.com");
        tvLocation.setText("Kathmandu, Nepal");

        // Set click listeners to navigate
        btnEditProfile.setOnClickListener(v -> navigateToFragment(R.id.action_profile_to_editProfileFragment));
        btnRequestBlood.setOnClickListener(v -> navigateToFragment(R.id.action_profile_to_requestBloodFragment));
        btnChangeLocation.setOnClickListener(v -> navigateToFragment(R.id.action_profile_to_changeLocationFragment));
        btnDonateRequest.setOnClickListener(v -> navigateToFragment(R.id.action_profile_to_donateRequestFragment));

        return view;

    }

    private void navigateToFragment(int actionId) {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(actionId);

    }
}
