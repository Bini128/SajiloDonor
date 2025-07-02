package com.example.donorblood.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ImageView;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.donorblood.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    TextView tvName, tvEmail, tvLocation;
    LinearLayout btnEditProfile, btnDonateRequest;
    ImageView profileImageView;
    Switch switchActivate;
    RadioGroup rgInactiveReasons;

    String userEmail;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvemail);
        tvLocation = view.findViewById(R.id.tvLocation);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        profileImageView = view.findViewById(R.id.profileImageView);
        btnDonateRequest = view.findViewById(R.id.btnDonateRequest);
        switchActivate = view.findViewById(R.id.switchActivate);
        SharedPreferences prefs = getActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userEmail = prefs.getString("email", "");
        rgInactiveReasons = view.findViewById(R.id.radioGroupReasons);

        tvEmail.setText(userEmail);
        fetchUserData(userEmail);
        checkDonationStatus(userEmail);

        btnDonateRequest.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_fragment);
            navController.navigate(R.id.donateRequestFragment);
        });

        btnEditProfile.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_fragment);
            navController.navigate(R.id.editProfileFragment);
        });
        switchActivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                canActivate(userEmail, can -> {
                    if (can) {
                        if (rgInactiveReasons != null) {
                            rgInactiveReasons.setVisibility(View.GONE); //
                        }
                        updateActiveStatus("active", "");
                    } else {
                        switchActivate.setChecked(false);
                        Toast.makeText(getContext(), "You can activate only 3 months after your last donation.", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                showInactiveReasonDialog();
            }
        });


        return view;
    }
    private void showInactiveReasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialoge_inactive_status, null);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupReasons);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitReason);

        AlertDialog dialog = builder.setView(dialogView).create();
        dialog.setCancelable(false); // Disable outside/cancel closing
        dialog.show();

        btnSubmit.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please select a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = dialogView.findViewById(selectedId);
            String reason = selectedRadio.getText().toString();
            updateActiveStatus("inactive", reason);
            dialog.dismiss();
        });
    }


    private void loadUserProfileImage(String profileImagePath) {
        if (profileImagePath != null && !profileImagePath.trim().isEmpty()) {
            if (profileImagePath.startsWith("/")) {
                profileImagePath = profileImagePath.substring(1);
            }
            String imageUrl = "http://192.168.18.17/Sajilodonor/" + profileImagePath;
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .transform(new CircleCrop())
                    .into(profileImageView);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.profile)
                    .transform(new CircleCrop())
                    .into(profileImageView);
        }
    }

    private void fetchUserData(String email) {
        String url = "http://192.168.18.17/Sajilodonor/get_user.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            String name = jsonObject.getString("name");
                            String address = jsonObject.getString("address");
                            String profileImagePath = jsonObject.optString("profile_image", "");

                            tvName.setText(name);
                            tvLocation.setText(address);
                            loadUserProfileImage(profileImagePath);

                            boolean isDonor = jsonObject.optInt("is_donor", 0) == 1;
                            if (isDonor) {
                                switchActivate.setVisibility(View.VISIBLE);
                            } else {
                                switchActivate.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
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
    public interface ActivationCheckCallback {
        void onResult(boolean canActivate);
    }

    private void canActivate(String email, ActivationCheckCallback callback) {
        String url = "http://192.168.18.17/Sajilodonor/check_activation_eligibility.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean allowed = obj.getBoolean("can_activate");
                        callback.onResult(allowed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onResult(false);
                    }
                },
                error -> {
                    callback.onResult(false);
                    Toast.makeText(getContext(), "Network error while checking activation", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void checkDonationStatus(String email) {
        String url = "http://192.168.18.17/Sajilodonor/check_donation_status.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("donated")) {
                            switchActivate.setVisibility(View.VISIBLE);
                        } else {
                            switchActivate.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Check donation error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {}
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", email);
                return map;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }



    private void updateActiveStatus(String status, String reason) {
        String url = "http://192.168.18.17/Sajilodonor/update_status.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Parse error", Toast.LENGTH_SHORT).show();
                    }                },
                error -> Toast.makeText(getContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("email", userEmail);
                map.put("status", status);
                map.put("reason", reason);
                return map;
            }
        };
        Volley.newRequestQueue(requireContext()).add(request);
    }
}
