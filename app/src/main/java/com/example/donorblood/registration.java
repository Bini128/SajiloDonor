package com.example.donorblood;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donorblood.database.DBHelper;
import com.example.donorblood.database.HashUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;

public class registration extends AppCompatActivity {

    EditText editName, editCitizenship, editPhoneNumber, editAddress, editEmail, editDOB,
            editPassword, editConfirmPassword;
    RadioGroup genderGroup;
    Button btnNextStep1;

    private static final int BLOODGROUP_REQUEST_CODE = 100;
    private String selectedBloodGroup = "";
    private String HashPassword="";

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        EdgeToEdge.enable(this);
        dbHelper = new DBHelper(this);

        editName = findViewById(R.id.editName);
        editCitizenship = findViewById(R.id.editCitizenship);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editaddress);
        editEmail = findViewById(R.id.editEmail);
        editDOB = findViewById(R.id.editDOB);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        genderGroup = findViewById(R.id.genderGroup);
        btnNextStep1 = findViewById(R.id.btnNextStep1);

        editDOB.setOnClickListener(v -> showDatePicker());

        btnNextStep1.setOnClickListener(v -> {
            try {
                if (validateInput()) {
                    // Start blood group selection activity
                    Intent intent = new Intent(registration.this, bloodgroup.class);
                    startActivityForResult(intent, BLOODGROUP_REQUEST_CODE);
                }
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 18;  // Example minimum age 18
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        editDOB.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year, month, day);
        datePickerDialog.show();
    }

    private boolean validateInput() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (TextUtils.isEmpty(editName.getText().toString().trim())) {
            editName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(editCitizenship.getText().toString().trim())) {
            editCitizenship.setError("Citizenship is required");
            return false;
        }
        if (TextUtils.isEmpty(editPhoneNumber.getText().toString().trim()) || editPhoneNumber.length() < 7) {
            editPhoneNumber.setError("Valid phone number is required");
            return false;
        }
        if (TextUtils.isEmpty(editAddress.getText().toString().trim())) {
            editAddress.setError("Address is required");
            return false;
        }
        if (TextUtils.isEmpty(editEmail.getText().toString().trim()) ||
                !Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString().trim()).matches()) {
            editEmail.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(editDOB.getText().toString().trim())) {
            editDOB.setError("DOB is required");
            return false;
        }
        if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editPassword.getText().toString()) || editPassword.length() < 6) {
            editPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (!editPassword.getText().toString().equals(editConfirmPassword.getText().toString())) {
            editConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BLOODGROUP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedBloodGroup = data.getStringExtra("selectedBloodGroup");
            saveUserToDb();
        }
    }

    private void saveUserToDb() {
        String name = editName.getText().toString().trim();
        String citizenship = editCitizenship.getText().toString().trim();
        String phone = editPhoneNumber.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String dob = editDOB.getText().toString().trim();
        int genderId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(genderId);
        String gender = selectedGenderButton.getText().toString();

        String rawPassword = editPassword.getText().toString();
        String hashedPassword;

        try {
            byte[] salt = HashUtil.generateSalt();
            hashedPassword = HashUtil.hashPassword(rawPassword, salt);
        } catch (Exception e) {
            Toast.makeText(this, "Password hashing failed", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertUser(name, citizenship, phone, address, email, dob, gender, hashedPassword, selectedBloodGroup);

        if (inserted) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, login.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
        }
    }

}
