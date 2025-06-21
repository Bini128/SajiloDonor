package com.example.donorblood;  

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donorblood.database.DBHelper;

public class login extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    ImageView passwordToggle;
    Button loginButton;
    TextView forgotPassword, signUpText;

    boolean isPasswordVisible = false;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordToggle = findViewById(R.id.passwordToggle);
        loginButton = findViewById(R.id.loginButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        signUpText = findViewById(R.id.signUpText);

        passwordToggle.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye);
                isPasswordVisible = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordToggle.setImageResource(R.drawable.ic_eye_off);
                isPasswordVisible = true;
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        loginButton.setOnClickListener(v -> validateLogin());

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registration.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        );
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

        boolean validUser = dbHelper.checkUser(email, password);

        if (validUser) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // ✅ Save login status
            SharedPreferences preferences = getSharedPreferences("login_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putString("userEmail", email); // optional: store user identity
            editor.apply();

            // ✅ Ask if user wants to change location
            new AlertDialog.Builder(this)
                    .setTitle("Change Location")
                    .setMessage("Do you want to change your location?")
                    .setPositiveButton("Allow", (dialogInterface, i) -> {
                        Intent intent = new Intent(login.this, map_picker.class);
                        startActivity(intent);
                        finish(); // so user doesn't return to login
                    })
                    .setNegativeButton("Deny", (dialogInterface, i) -> {
                        Intent intent = new Intent(login.this, dashboard.class);
                        intent.putExtra("lat", 28.3949);
                        intent.putExtra("lon", 84.1240);
                        startActivity(intent);
                        finish(); // so user doesn't return to login
                    })
                    .setCancelable(false)
                    .show();

        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
