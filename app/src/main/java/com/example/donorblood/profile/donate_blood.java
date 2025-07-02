package com.example.donorblood.profile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.donorblood.R;
import com.example.donorblood.donation;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class donate_blood extends Fragment {


        EditText editDonorName, editEmail, editBloodGroup, editDOB, editPhone;
        Button btnConfirm;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_donate_blood, container, false);

            editDonorName = view.findViewById(R.id.editDonorName);
            editEmail = view.findViewById(R.id.editEmail);
            editBloodGroup = view.findViewById(R.id.editBloodGroup);
            editDOB = view.findViewById(R.id.editDOB);
            editPhone = view.findViewById(R.id.editPhone);
            btnConfirm = view.findViewById(R.id.btnConfirm);

            SharedPreferences prefs = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
            String email = prefs.getString("email", "");

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return view;
            }

            fetchUserData(email);

            btnConfirm.setOnClickListener(v -> {
                String dobStr = editDOB.getText().toString().trim();
                int age = calculateAgeFromDOB(dobStr);
                if (age < 18) {
                    Toast.makeText(getContext(), "You are not eligible to donate blood.", Toast.LENGTH_SHORT).show();
                } else {
                    checkIfAlreadyDonated();
                }
            });

            return view;
        }
    private void checkIfAlreadyDonated() {
        String url = "http://192.168.18.17/Sajilodonor/check_donation_status.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("exists")) {
                            Toast.makeText(getContext(), "You have already registered as a donor.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Start donation activity
                            Intent intent = new Intent(getActivity(), donation.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                SharedPreferences prefs = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                String userEmail = prefs.getString("email", "");
                map.put("email", userEmail);
                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

        private void fetchUserData(String email) {
            String url = "http://192.168.18.17/Sajilodonor/get_user.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                editDonorName.setText(json.getString("name"));
                                editEmail.setText(email);
                                editDOB.setText(json.getString("dob"));
                                editPhone.setText(json.getString("phone"));
                                editBloodGroup.setText(json.getString("bloodgroup"));
                            } else {
                                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    return params;
                }
            };

            Volley.newRequestQueue(requireContext()).add(request);
        }

        private int calculateAgeFromDOB(String dobString) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date dob = sdf.parse(dobString);
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dob);

                Calendar today = Calendar.getInstance();

                int age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR);
                if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }
                return age;
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Invalid DOB format", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }
    }
