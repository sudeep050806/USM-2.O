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
import com.google.android.material.card.MaterialCardView;

/**
 * LoginActivity - Role-based login selection
 * User chooses Student/Teacher/Admin role before entering credentials
 */
public class LoginActivity extends AppCompatActivity {

    private MaterialCardView cardStudent, cardTeacher, cardAdmin;
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    
    // Selected role
    private String selectedRole = "student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initHelpers();
        setupListeners();
    }

    private void initViews() {
        cardStudent = findViewById(R.id.cardStudent);
        cardTeacher = findViewById(R.id.cardTeacher);
        cardAdmin = findViewById(R.id.cardAdmin);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void setupListeners() {
        // Student card click
        cardStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "student";
                highlightCard(cardStudent);
                navigateToCredentialsScreen("student");
            }
        });

        // Teacher card click
        cardTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "teacher";
                highlightCard(cardTeacher);
                navigateToCredentialsScreen("teacher");
            }
        });

        // Admin card click
        cardAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "admin";
                highlightCard(cardAdmin);
                navigateToCredentialsScreen("admin");
            }
        });

        // Create account button
        findViewById(R.id.btnCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to role selection for signup
                startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class));
            }
        });
    }

    /**
     * Highlight selected role card
     */
    private void highlightCard(MaterialCardView selected) {
        // Reset all cards
        cardStudent.setStrokeWidth(0);
        cardTeacher.setStrokeWidth(0);
        cardAdmin.setStrokeWidth(0);
        
        // Highlight selected (white border)
        selected.setStrokeWidth(4);
        selected.setStrokeColor(getColor(R.color.card_background));
    }

    /**
     * Navigate to credentials entry screen
     */
    private void navigateToCredentialsScreen(String role) {
        Intent intent = new Intent(LoginActivity.this, UserCredentialsActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset card highlights
        cardStudent.setStrokeWidth(0);
        cardTeacher.setStrokeWidth(0);
        cardAdmin.setStrokeWidth(0);
    }
}