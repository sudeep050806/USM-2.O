package com.example.universitysports.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.dashboard.AdminDashboardActivity;
import com.example.universitysports.dashboard.StudentDashboardActivity;
import com.example.universitysports.dashboard.TeacherDashboardActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.SessionManager;
import com.example.universitysports.models.User;
import com.example.universitysports.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * UserCredentialsActivity - Email & password entry for selected role
 * Handles both login and signup navigation
 */
public class UserCredentialsActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private TextView tvSelectedRole, tvSignup;
    private ProgressBar progressBar;

    private String selectedRole;
    private DBHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_credentials);

        // Get role from intent
        selectedRole = getIntent().getStringExtra("role");
        if (selectedRole == null) selectedRole = "student";

        initViews();
        initHelpers();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvSelectedRole = findViewById(R.id.tvSelectedRole);
        tvSignup = findViewById(R.id.tvSignup);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
    }

    private void setupUI() {
        // Display selected role
        String roleDisplay = selectedRole.substring(0, 1).toUpperCase() + selectedRole.substring(1);
        tvSelectedRole.setText(roleDisplay);
    }

    private void setupListeners() {
        // Login button
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Forgot password - temporarily show message
        findViewById(R.id.tvForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserCredentialsActivity.this, 
                    "Contact admin to reset password", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to Signup
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCredentialsActivity.this, SignupActivity.class);
                intent.putExtra("role", selectedRole);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Validate and attempt login
     */
    private void attemptLogin() {
        ValidationUtils.clearError(tilEmail);
        ValidationUtils.clearError(tilPassword);

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate email
        if (ValidationUtils.isRequiredField(email)) {
            ValidationUtils.setError(tilEmail, "Email is required");
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            ValidationUtils.setError(tilEmail, "Invalid email format");
            return;
        }

        // Validate password
        if (ValidationUtils.isRequiredField(password)) {
            ValidationUtils.setError(tilPassword, "Password is required");
            return;
        }

        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnLogin).setEnabled(false);

        // Simulate network delay for better UX
        findViewById(R.id.btnLogin).postDelayed(new Runnable() {
            @Override
            public void run() {
                validateCredentials(email, password);
            }
        }, 800);
    }

    /**
     * Validate credentials against database
     */
    private void validateCredentials(String email, String password) {
        User user = dbHelper.validateLogin(email, password);

        if (user != null) {
            // Check role matches
            if (!user.getRole().equals(selectedRole) && !"admin".equals(selectedRole)) {
                progressBar.setVisibility(View.GONE);
                findViewById(R.id.btnLogin).setEnabled(true);
                Toast.makeText(this, 
                    "This account is not registered as " + selectedRole, 
                    Toast.LENGTH_LONG).show();
                return;
            }

            // Check verification
            if (!user.isVerified() && !"admin".equals(user.getRole())) {
                progressBar.setVisibility(View.GONE);
                findViewById(R.id.btnLogin).setEnabled(true);
                
                // Send OTP for unverified accounts
                sendOTPAndRedirect(email);
                return;
            }

            // Create session
            sessionManager.createLoginSession(user);

            progressBar.setVisibility(View.GONE);
            findViewById(R.id.btnLogin).setEnabled(true);

            // Redirect based on role
            Class<?> activity;
            if ("admin".equals(user.getRole())) {
                activity = AdminDashboardActivity.class;
            } else if ("teacher".equals(user.getRole())) {
                activity = TeacherDashboardActivity.class;
            } else {
                activity = StudentDashboardActivity.class;
            }

            startActivity(new Intent(UserCredentialsActivity.this, activity));
            finish();

        } else {
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.btnLogin).setEnabled(true);
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Send OTP and redirect to verification
     */
    private void sendOTPAndRedirect(String email) {
        // Generate OTP using AppConfig
        String otp = com.example.universitysports.helpers.AppConfig.generateOTP();
        
        // Save to DB with expiry
        long expiryTime = System.currentTimeMillis() + com.example.universitysports.helpers.AppConfig.OTP_EXPIRY_TIME;
        com.example.universitysports.models.OTP otpModel = new com.example.universitysports.models.OTP(email, otp, expiryTime);
        dbHelper.saveOTP(otpModel);
        
        // Send email
        new com.example.universitysports.helpers.EmailSender().sendOTPEmail(email, otp);
        
        // Navigate to OTP verification
        Intent intent = new Intent(UserCredentialsActivity.this, OTPVerificationActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("isLogin", true);
        intent.putExtra("role", selectedRole);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}