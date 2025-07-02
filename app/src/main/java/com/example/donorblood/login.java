package com.example.donorblood;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View signUpText = findViewById(R.id.signUpText);

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registration.class);
            startActivity(intent);
        });

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> validateLogin());
        EditText editPassword = findViewById(R.id.passwordEditText);
        ImageView eyeToggle = findViewById(R.id.passwordToggle);

        eyeToggle.setOnClickListener(v -> {
            if (editPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Show password
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye); // icon for "eye open"
            } else {
                // Hide password
                editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                eyeToggle.setImageResource(R.drawable.ic_eye_off); // icon for "eye closed"
            }
            // Move cursor to the end
            editPassword.setSelection(editPassword.getText().length());
        });

    }

    private void validateLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        loginWithVolley(email, password);
    }

    private void loginWithVolley(String email, String password) {
        String url = "http://192.168.18.17/Sajilodonor/login.php"; // Change to your server IP
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                            SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", email);        // Save with key "email"
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();                           // Apply once after all puts

                            new AlertDialog.Builder(this)
                                    .setTitle("Change Location")
                                    .setMessage("Do you want to change your location?")
                                    .setPositiveButton("Allow", (dialogInterface, i) -> {
                                        Intent intent = new Intent(login.this, map_picker.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                                        // Redirect to dashboard
                                        Intent intent = new Intent(login.this, dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .show();
                        } else {
                            // Handle login failure
                            Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Invalid email or Password: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
