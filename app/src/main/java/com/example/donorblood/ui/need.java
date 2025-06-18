package com.example.donorblood.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class need extends Fragment {
    private RecyclerView recyclerView;
    private DonorAdapter adapter;
    private DBHelper dbHelper;
    private List<User> donorList = new ArrayList<>();

    private final String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_need, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDonors);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        dbHelper = new DatabaseHelper(getContext());
//        donorList = dbHelper.getUsersByBloodType("A+"); // or use getUsersByBloodType("A+")
//        adapter = new DonorAdapter(getContext(), donorList);
//        recyclerView.setAdapter(adapter);


        // Initially show random donors
        // loadDonors(getRandomBloodType());
//
//        setupButton(view, R.id.btnAPlus, "A+");
//        setupButton(view, R.id.btnAMinus, "A-");
//        setupButton(view, R.id.btnBPlus, "B+");
//        setupButton(view, R.id.btnBminus, "B-");
//        setupButton(view, R.id.btnOPlus, "O+");
//        setupButton(view, R.id.btnOminus, "O-");
//        setupButton(view, R.id.btnABPlus, "AB+");
//        setupButton(view, R.id.btnABminus, "AB-");
//
//        return view;
//    }

//    private void setupButton(View view, int id, String bloodType) {
//        Button button = view.findViewById(id);
//        button.setOnClickListener(v -> loadDonors(bloodType));
//    }

//    private void loadDonors(String bloodType) {
//        donorList.clear();
//        donorList.addAll(dbHelper.getUsersByBloodType(bloodType));
//        adapter.notifyDataSetChanged();
//    }

//    private String getRandomBloodType() {
//        return bloodTypes[new Random().nextInt(bloodTypes.length)];
//    }

        //}
        return view;
    }
}
