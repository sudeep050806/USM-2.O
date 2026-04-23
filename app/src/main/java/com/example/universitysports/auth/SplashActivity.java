package com.example.universitysports.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.dashboard.AdminDashboardActivity;
import com.example.universitysports.dashboard.StudentDashboardActivity;
import com.example.universitysports.dashboard.TeacherDashboardActivity;
import com.example.universitysports.helpers.SessionManager;

/**
 * SplashActivity - Initial launcher screen
 * Checks login state and redirects appropriately
 */
public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = new SessionManager(this);
        progressBar = findViewById(R.id.progressBar);

        // Simulate loading for better UX (1.5 seconds)
        progressBar.setVisibility(ProgressBar.VISIBLE);
        
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserSession();
            }
        }, 1500);
    }

    /**
     * Check if user is already logged in
     * Redirect based on role
     */
    private void checkUserSession() {
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getUserRole();
            Class<?> activityClass;

            if ("admin".equals(role)) {
                activityClass = AdminDashboardActivity.class;
            } else if ("teacher".equals(role)) {
                activityClass = TeacherDashboardActivity.class;
            } else {
                activityClass = StudentDashboardActivity.class;
            }

            startActivity(new Intent(SplashActivity.this, activityClass));
            finish();
        } else {
            // No session, go to login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}