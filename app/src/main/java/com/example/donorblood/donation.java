package com.example.donorblood;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.donorblood.ui.Profile;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class donation extends AppCompatActivity {

    EditText editLastDonationDate;
    Spinner spinnerPreviousDonations;
    RadioGroup rgDisease, rgAvailability;
    ImageView imageFrontPreview, imageBackPreview;
    Button btnUploadFront, btnUploadBack, btnSubmit;
    CheckBox cbPrivacyPolicy;

    String encodedFrontImage = "", encodedBackImage = "";
    String userEmail;
    static final int PICK_FRONT = 1, PICK_BACK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        // Initialize views
        editLastDonationDate = findViewById(R.id.editLastDonationDate);
        spinnerPreviousDonations = findViewById(R.id.spinnerPreviousDonations);
        rgDisease = findViewById(R.id.rgDisease);
        rgAvailability = findViewById(R.id.rgAvailability);
        imageFrontPreview = findViewById(R.id.imageFrontPreview);
        imageBackPreview = findViewById(R.id.imageBackPreview);
        btnUploadFront = findViewById(R.id.btnUploadFront);
        btnUploadBack = findViewById(R.id.btnUploadBack);
        cbPrivacyPolicy = findViewById(R.id.cbPrivacyPolicy);
        btnSubmit = findViewById(R.id.btnSubmitDonation);

        userEmail = getSharedPreferences("UserSession", MODE_PRIVATE).getString("email", "");

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"0", "1", "2", "3", "4", "5+"});
        spinnerPreviousDonations.setAdapter(adapter);

        // Date picker
        editLastDonationDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d);
                editLastDonationDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnUploadFront.setOnClickListener(v -> pickImage(PICK_FRONT));
        btnUploadBack.setOnClickListener(v -> pickImage(PICK_BACK));
        btnSubmit.setOnClickListener(v -> submitForm());
    }

    private void pickImage(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), code);
    }

    @Override
    protected void onActivityResult(int code, int result, @Nullable Intent data) {
        super.onActivityResult(code, result, data);
        if (result == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                String base64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                if (code == PICK_FRONT) {
                    encodedFrontImage = base64;
                    imageFrontPreview.setImageBitmap(bitmap);
                    imageFrontPreview.setVisibility(View.VISIBLE);
                } else {
                    encodedBackImage = base64;
                    imageBackPreview.setImageBitmap(bitmap);
                    imageBackPreview.setVisibility(View.VISIBLE);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitForm() {
        String date = editLastDonationDate.getText().toString().trim();
        String donations = spinnerPreviousDonations.getSelectedItem().toString().trim();

        String disease = rgDisease.getCheckedRadioButtonId() == R.id.rbDiseaseYes ? "Yes" : "No";
        String availability = rgAvailability.getCheckedRadioButtonId() == R.id.rbAvailableYes ? "Yes" : "No";

        if (date.isEmpty() || encodedFrontImage.isEmpty() || encodedBackImage.isEmpty()) {
            Toast.makeText(this, "Please fill all fields and upload images.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbPrivacyPolicy.isChecked()) {
            Toast.makeText(this, "You must accept the Privacy Policy to proceed.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.18.17/Sajilodonor/submit_donation.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        if (obj.getBoolean("success")) {
                            Intent intent = new Intent(this, Profile.class);
                            Toast.makeText(this,"Thanks for registration", Toast.LENGTH_SHORT).show();
                            intent.putExtra("navigateTo", "profile");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Parsing error occurred", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> m = new HashMap<>();
                m.put("email", userEmail);
                m.put("last_donation_date", date);
                m.put("total_donations", donations);
                m.put("had_disease_before", disease);
                m.put("has_disease_today", disease); // optional or you can remove
                m.put("is_available_anytime", availability);
                m.put("citizen_front", encodedFrontImage);
                m.put("citizen_back", encodedBackImage);
                m.put("agreed_policy", "Yes");
                return m;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }
}
