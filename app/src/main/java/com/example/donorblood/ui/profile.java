package com.example.donorblood.ui;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.donorblood.database.DBHelper;
import com.example.donorblood.R;
import com.example.donorblood.User;
import com.example.donorblood.profile.donate_blood;
import com.example.donorblood.profile.location;
import com.example.donorblood.profile.need_blood;

import org.jetbrains.annotations.Nullable;

public class profile extends Fragment {

    private TextView tvName, tvEmail, tvLocation;
    private String userEmail;
    private DBHelper dbHelper;

    public profile() {
        // Required empty public constructor
    }

    // You can pass user email as argument to this fragment
    public static profile newInstance(String email) {
        profile fragment = new profile();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DBHelper(requireContext());

        userEmail = getArguments() != null ? getArguments().getString("email") : "";

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvemail);
        tvLocation = view.findViewById(R.id.tvLocation);

        // Load user info from DB
        loadUserProfile();

        // Setup click listeners for buttons in your layout
        view.findViewById(R.id.btnRequestBlood).setOnClickListener(this::onHistoryClicked);
        view.findViewById(R.id.btnDonateRequest).setOnClickListener(this::onRequestClicked);
        view.findViewById(R.id.btnChangeLocation).setOnClickListener(this::onEditLocationClicked);

        return view;
    }

    private void loadUserProfile() {
        User user = dbHelper.getUserByEmail(userEmail);
        if (user != null) {
            tvName.setText(user.getName());
            tvEmail.setText(user.getEmail());
            tvLocation.setText(user.getAddress());
        } else {
            Log.d("ProfileFragment", "User not found for email: " + userEmail);
        }
    }

    // Button click handlers (called from onClick)
    public void onHistoryClicked(View view) {
        // Replace a child container with NeedBloodFragment
        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_fragment_container, new need_blood())
                .addToBackStack(null)
                .commit();
    }

    public void onRequestClicked(View view) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_fragment_container, new donate_blood())
                .addToBackStack(null)
                .commit();
    }

    public void onEditLocationClicked(View view) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.profile_fragment_container, new location())
                .addToBackStack(null)
                .commit();
    }
}


