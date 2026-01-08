package com.example.eventmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignUp;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Login attempt: " + email);

        // First check if it's the hardcoded admin (both email and username)
        if ((email.equals("admin") || email.equals("admin@event.com")) && password.equals("admin123")) {
            Log.d(TAG, "Admin login successful");
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check database for regular users
        try {
            if (dbHelper.checkUser(email, password)) {
                Log.d(TAG, "User found in database");
                String role = dbHelper.getUserRole(email);
                int userId = dbHelper.getUserId(email);

                Log.d(TAG, "User role: " + role + ", User ID: " + userId);

                Intent intent;
                if (role != null && role.equals("admin")) {
                    Log.d(TAG, "Redirecting to Admin Dashboard");
                    intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                } else {
                    Log.d(TAG, "Redirecting to User Dashboard with ID: " + userId);
                    intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                    intent.putExtra("USER_ID", userId);
                }
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "Invalid credentials");
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Login error: " + e.getMessage(), e);
            Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}