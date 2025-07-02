package com.example.donorblood;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {

    EditText editName, editCitizenship, editPhone, editEmail, editDOB, editPassword, editConfirmPassword;
    RadioGroup genderGroup;
    Spinner spinnerAddress;
    Button btnRegister;
    ImageView showPassword,confirmPassword;

    private static final int BLOODGROUP_REQUEST_CODE = 100;
    private String selectedBloodGroup = "";

    // Store user inputs
    String name, citizenship, phone, address, email, dob, gender, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editName = findViewById(R.id.editName);
        editCitizenship = findViewById(R.id.editCitizenship);
        editPhone = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editDOB = findViewById(R.id.editDOB);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        genderGroup = findViewById(R.id.genderGroup);
        spinnerAddress = findViewById(R.id.spinnerAddress);
        btnRegister = findViewById(R.id.btnNextStep1);
        showPassword = findViewById(R.id.showpassword);
        confirmPassword = findViewById(R.id.confirm_password);

// Toggle for Password field
        showPassword.setOnClickListener(v -> {
            if (editPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPassword.setImageResource(R.drawable.ic_eye_off); // Change to eye-off icon
            } else {
                // Hide password
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPassword.setImageResource(R.drawable.ic_eye); // Change to eye icon
            }
            editPassword.setSelection(editPassword.getText().length());
        });

// Toggle for Confirm Password field
        confirmPassword.setOnClickListener(v -> {
            if (editConfirmPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                editConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confirmPassword.setImageResource(R.drawable.ic_eye_off);
            } else {
                // Hide password
                editConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confirmPassword.setImageResource(R.drawable.ic_eye);
            }
            editConfirmPassword.setSelection(editConfirmPassword.getText().length());
        });


        // Setup Spinner for address
        String[] addressOptions = {"Kathmandu", "Pokhara", "Lalitpur", "Bhaktapur"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addressOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(adapter);

        // Setup DOB EditText - show DatePicker on click, disable keyboard
        editDOB.setInputType(InputType.TYPE_NULL);
        editDOB.setFocusable(false);
        editDOB.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - 18;  // minimum age 18
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    registration.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Display format: DD/MM/YYYY
                        String displayDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        editDOB.setText(displayDate);

                        // Save format: YYYY-MM-DD (store this in a variable or hidden field)
                        String dobToSave = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDOB.setTag(dobToSave); // optional: store it for sending to server
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                // Launch blood group selection activity
                Intent intent = new Intent(this, bloodgroup.class);
                startActivityForResult(intent, BLOODGROUP_REQUEST_CODE);
            }
        });
    }

    private boolean validateInputs() {
        name = editName.getText().toString().trim();
        citizenship = editCitizenship.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        address = spinnerAddress.getSelectedItem().toString();
        email = editEmail.getText().toString().trim();
        dob = editDOB.getText().toString().trim();
        password = editPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            editName.setError("Name required");
            return false;
        }
        if (TextUtils.isEmpty(citizenship)) {
            editCitizenship.setError("Citizenship required");
            return false;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 7) {
            editPhone.setError("Valid phone required");
            return false;
        }
        // Address spinner is always valid

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Valid email required");
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            editDOB.setError("DOB required");
            return false;
        }
        if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editPassword.setError("Password min 6 chars");
            return false;
        }
        if (!password.equals(editConfirmPassword.getText().toString())) {
            editConfirmPassword.setError("Passwords do not match");
            return false;
        }

        // Get gender string
        int genderId = genderGroup.getCheckedRadioButtonId();
        RadioButton genderBtn = findViewById(genderId);
        gender = genderBtn.getText().toString();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BLOODGROUP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedBloodGroup = data.getStringExtra("selectedBloodGroup");
            sendRegistrationRequest();
        }
    }

    private void sendRegistrationRequest() {
        // Use adb reverse so localhost works; else replace with PC IP if connected via Wi-Fi
        String url = "http://192.168.18.17/Sajilodonor/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        if (obj.getBoolean("success")) {
                            // Registration success: go to login
                            startActivity(new Intent(this, login.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("citizenship", citizenship);
                params.put("phone", phone);
                params.put("address", address);
                params.put("email", email);
                params.put("dob", dob);
                params.put("gender", gender);
                params.put("password", password);
                params.put("bloodgroup", selectedBloodGroup);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
