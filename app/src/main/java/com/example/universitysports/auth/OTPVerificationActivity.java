package com.example.universitysports.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.dashboard.AdminDashboardActivity;
import com.example.universitysports.dashboard.StudentDashboardActivity;
import com.example.universitysports.dashboard.TeacherDashboardActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.helpers.SessionManager;
import com.example.universitysports.models.OTP;
import com.example.universitysports.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OTPVerificationActivity - Advanced OTP UI with 6 input boxes
 * Features:
 * - Auto-focus between boxes
 * - Auto-backspace support
 * - 2-minute expiry countdown
 * - Max 3 attempts
 * - Resend with 30s cooldown
 * - Disabled button during verification
 * - Thread-safe email sending via ExecutorService
 */
public class OTPVerificationActivity extends AppCompatActivity {

    // 6 OTP input fields
    private TextInputEditText[] otpFields = new TextInputEditText[6];
    private TextView tvTimer, tvResendOtp, tvAttempts, tvErrorMessage;
    private View btnVerify, progressBar;

    private String email;
    private boolean isNewUser;
    private String role; // For new signup
    private DBHelper dbHelper;
    private SessionManager sessionManager;
    private EmailSender emailSender;

    private CountDownTimer countDownTimer;
    private ExecutorService executorService;
    private long timeLeftInMillis = com.example.universitysports.helpers.AppConfig.OTP_EXPIRY_TIME;
    
    // OTP attempt tracking
    private int currentAttempts = 0;
    private static final int MAX_ATTEMPTS = com.example.universitysports.helpers.AppConfig.MAX_OTP_ATTEMPTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Get intent data
        email = getIntent().getStringExtra("email");
        isNewUser = getIntent().getBooleanExtra("isNewUser", false);
        role = getIntent().getStringExtra("role"); // For signup

        // Initialize
        initHelpers();
        initViews();
        setupUI();
        setupListeners();
        startTimer();
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
        sessionManager = new SessionManager(this);
        emailSender = new EmailSender();
        executorService = Executors.newSingleThreadExecutor();
    }

    private void initViews() {
        // Find all 6 OTP fields
        otpFields[0] = findViewById(R.id.etOtp1);
        otpFields[1] = findViewById(R.id.etOtp2);
        otpFields[2] = findViewById(R.id.etOtp3);
        otpFields[3] = findViewById(R.id.etOtp4);
        otpFields[4] = findViewById(R.id.etOtp5);
        otpFields[5] = findViewById(R.id.etOtp6);
        
        tvTimer = findViewById(R.id.tvTimer);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvAttempts = findViewById(R.id.tvAttempts);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnVerify = findViewById(R.id.btnVerify);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupUI() {
        TextView tvEmail = findViewById(R.id.tvEmail);
        tvEmail.setText(email);
        
        updateAttemptsDisplay();
        
        // Get current OTP attempts from DB
        OTP otpRecord = dbHelper.getOTPByEmail(email);
        if (otpRecord != null) {
            currentAttempts = otpRecord.getAttempts();
            updateAttemptsDisplay();
        }
    }

    private void setupListeners() {
        // Setup text watchers for each OTP field
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Move to next field
                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        }
                    } else if (s.length() == 0 && index > 0) {
                        // Backspace - move to previous field
                        otpFields[index - 1].requestFocus();
                        otpFields[index - 1].setText("");
                    }
                    checkComplete();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Verify button
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOTP();
            }
        });

        // Resend OTP
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvResendOtp.isEnabled()) {
                    resendOTP();
                }
            }
        });
    }

    /**
     * Check if all 6 fields are filled
     */
    private void checkComplete() {
        boolean allFilled = true;
        for (TextInputEditText field : otpFields) {
            if (field.getText().toString().trim().isEmpty()) {
                allFilled = false;
                break;
            }
        }
        btnVerify.setEnabled(allFilled);
    }

    /**
     * Get complete OTP string from all fields
     */
    private String getEnteredOTP() {
        StringBuilder sb = new StringBuilder();
        for (TextInputEditText field : otpFields) {
            sb.append(field.getText().toString().trim());
        }
        return sb.toString();
    }

    /**
     * Verify OTP against database
     */
    private void verifyOTP() {
        String otp = getEnteredOTP();
        if (otp.length() != 6) {
            showError("Enter complete 6-digit OTP");
            return;
        }

        setVerifyingState(true);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final DBHelper.OTPVerifyResult result = dbHelper.verifyOTP(email, otp);
                
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setVerifyingState(false);
                        
                        if (result.isSuccess()) {
                            // Verification successful
                            onOTPVerified();
                        } else {
                            // Show error
                            showError(result.getMessage());
                            currentAttempts++;
                            updateAttemptsDisplay();
                            
                            // Check if max attempts exceeded
                            if (currentAttempts >= MAX_ATTEMPTS) {
                                disableInputs();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Called after successful OTP verification
     */
    private void onOTPVerified() {
        if (isNewUser) {
            // Mark user as verified
            dbHelper.verifyUser(email);
            
            // Get user and create session
            User user = dbHelper.getUserByEmail(email);
            if (user != null) {
                user.setVerified(true);
                sessionManager.createLoginSession(user);
                
                Toast.makeText(this, "Account verified!", Toast.LENGTH_SHORT).show();
                
                // Navigate to appropriate dashboard
                navigateToDashboard(role);
            }
        } else {
            // Login flow
            User user = dbHelper.getUserByEmail(email);
            if (user != null) {
                sessionManager.createLoginSession(user);
                
                String userRole = user.getRole();
                navigateToDashboard(userRole);
            }
        }
        finish();
    }

    /**
     * Navigate based on role
     */
    private void navigateToDashboard(String userRole) {
        Class<?> activity;
        if ("admin".equals(userRole)) {
            activity = AdminDashboardActivity.class;
        } else if ("teacher".equals(userRole)) {
            activity = TeacherDashboardActivity.class;
        } else {
            activity = StudentDashboardActivity.class;
        }
        startActivity(new Intent(OTPVerificationActivity.this, activity));
        finish();
    }

    /**
     * Resend OTP
     */
    private void resendOTP() {
        // Generate new OTP
        String newOtp = com.example.universitysports.helpers.AppConfig.generateOTP();
        long newExpiry = System.currentTimeMillis() + com.example.universitysports.helpers.AppConfig.OTP_EXPIRY_TIME;
        
        // Save to DB (replaces old)
        OTP otpModel = new OTP(email, newOtp, newExpiry);
        dbHelper.saveOTP(otpModel);
        
        // Send email
        emailSender.sendOTPEmail(email, newOtp);
        
        // Reset timer
        timeLeftInMillis = com.example.universitysports.helpers.AppConfig.OTP_EXPIRY_TIME;
        startTimer();
        
        // Clear fields
        for (TextInputEditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
        
        Toast.makeText(this, "New OTP sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Start countdown timer for resend cooldown
     */
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                tvResendOtp.setEnabled(true);
                tvTimer.setVisibility(View.GONE);
                tvResendOtp.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    /**
     * Update timer text
     */
    private void updateTimerDisplay() {
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        int minutes = (int) (timeLeftInMillis / (1000 * 60));
        tvTimer.setText(String.format("Resend in %02d:%02d", minutes, seconds));
    }

    /**
     * Update attempts display
     */
    private void updateAttemptsDisplay() {
        tvAttempts.setText(String.format("Attempts: %d/%d", currentAttempts, MAX_ATTEMPTS));
        
        // Change color if low attempts
        if (currentAttempts >= 2) {
            tvAttempts.setTextColor(getColor(R.color.error));
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        
        // Clear fields and refocus first
        for (TextInputEditText field : otpFields) {
            field.setText("");
        }
        otpFields[0].requestFocus();
    }

    /**
     * Set verifying state (disable UI)
     */
    private void setVerifyingState(boolean verifying) {
        btnVerify.setEnabled(!verifying);
        progressBar.setVisibility(verifying ? View.VISIBLE : View.GONE);
        
        if (verifying) {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }

    /**
     * Disable inputs when max attempts exceeded
     */
    private void disableInputs() {
        for (TextInputEditText field : otpFields) {
            field.setEnabled(false);
        }
        btnVerify.setEnabled(false);
        tvResendOtp.setEnabled(false);
        showError("Max attempts exceeded. Please contact support.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}