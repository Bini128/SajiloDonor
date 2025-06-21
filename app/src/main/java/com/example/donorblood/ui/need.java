package com.example.donorblood.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donorblood.database.DBHelper;
import com.example.donorblood.DonorAdapter;
import com.example.donorblood.R;
import com.example.donorblood.User;
import com.example.donorblood.database.DBHelper;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class need extends Fragment {

    private RecyclerView recyclerView;
    private DonorAdapter adapter;
    private DBHelper dbHelper;
    private List<User> donorList = new ArrayList<>();

    private final String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_need, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDonors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new DBHelper(getContext());

        // Initially show random donors
        loadDonors(getRandomBloodType());

        // Setup buttons for each blood group
        setupButton(view, R.id.btnAPlus, "A+");
        setupButton(view, R.id.btnAMinus, "A-");
        setupButton(view, R.id.btnBPlus, "B+");
        setupButton(view, R.id.btnBminus, "B-");
        setupButton(view, R.id.btnOPlus, "O+");
        setupButton(view, R.id.btnOminus, "O-");
        setupButton(view, R.id.btnABPlus, "AB+");
        setupButton(view, R.id.btnABminus, "AB-");

        return view;
    }

    private void setupButton(View view, int id, String bloodType) {
        Button button = view.findViewById(id);
        button.setOnClickListener(v -> loadDonors(bloodType));
    }

    private void loadDonors(String bloodType) {
        donorList = dbHelper.getUsersByBloodType(bloodType);
        adapter = new DonorAdapter(getContext(), donorList);
        recyclerView.setAdapter(adapter);
    }

    private String getRandomBloodType() {
        return bloodTypes[new Random().nextInt(bloodTypes.length)];
    }
}
