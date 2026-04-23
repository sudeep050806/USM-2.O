package com.example.universitysports.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.models.User;
import com.example.universitysports.utils.ValidationUtils;
import com.google.android.material.textfield.TextInputEditText;

/**
 * SignupActivity - Register new user (Student or Teacher)
 * Includes validation and OTP verification flow
 */
public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private TextView tvSelectedRole, tvChangeRole, tvLogin;
    private ProgressBar progressBar;

    private String selectedRole;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Get role from intent
        selectedRole = getIntent().getStringExtra("role");
        if (selectedRole == null) selectedRole = "student";

        initViews();
        initHelpers();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvSelectedRole = findViewById(R.id.tvSelectedRole);
        tvChangeRole = findViewById(R.id.tvChangeRole);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void setupUI() {
        String roleDisplay = selectedRole.substring(0, 1).toUpperCase() + selectedRole.substring(1);
        tvSelectedRole.setText(roleDisplay);

        // Change role click
        tvChangeRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to role selection
                startActivity(new Intent(SignupActivity.this, RoleSelectionActivity.class));
                finish();
            }
        });
    }

    private void setupListeners() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignup();
            }
        });
    }

    /**
     * Validate and register user
     */
    private void attemptSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Clear previous errors
        ValidationUtils.clearError(null); // TODO: Add TextInputLayout fields

        // Validate name
        if (ValidationUtils.isRequiredField(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        // Validate email
        if (ValidationUtils.isRequiredField(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        // Check email uniqueness
        if (dbHelper.isEmailExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            return;
        }

        // Validate phone
        if (ValidationUtils.isRequiredField(phone)) {
            etPhone.setError("Phone is required");
            etPhone.requestFocus();
            return;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            etPhone.setError("Invalid phone number (10-15 digits)");
            etPhone.requestFocus();
            return;
        }

        // Validate password
        if (ValidationUtils.isRequiredField(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Use 8+ chars with letters & numbers");
            etPassword.requestFocus();
            return;
        }

        // Confirm password
        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            etConfirmPassword.setError("Passwords don't match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Proceed with registration
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnSignup).setEnabled(false);

        // Simulate delay
        findViewById(R.id.btnSignup).postDelayed(new Runnable() {
            @Override
            public void run() {
                registerUser(name, email, phone, password);
            }
        }, 800);
    }

    /**
     * Register user and send OTP
     */
    private void registerUser(String name, String email, String phone, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRole(selectedRole);
        user.setVerified(false);

        long userId = dbHelper.registerUser(user);

        if (userId > 0) {
            // Generate and send OTP
            String otp = com.example.universitysports.helpers.AppConfig.generateOTP();
            long expiryTime = System.currentTimeMillis() + com.example.universitysports.helpers.AppConfig.OTP_EXPIRY_TIME;
            
            com.example.universitysports.models.OTP otpModel = new com.example.universitysports.models.OTP(email, otp, expiryTime);
            dbHelper.saveOTP(otpModel);
            
            emailSender.sendOTPEmail(email, otp);

            progressBar.setVisibility(View.GONE);
            findViewById(R.id.btnSignup).setEnabled(true);

            ValidationUtils.showLongToast(this, "Account created! Verify your email.");
            
            // Navigate to OTP verification
            Intent intent = new Intent(SignupActivity.this, OTPVerificationActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("isNewUser", true);
            intent.putExtra("role", selectedRole);
            startActivity(intent);
            finish();

        } else {
            progressBar.setVisibility(View.GONE);
            findViewById(R.id.btnSignup).setEnabled(true);
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}