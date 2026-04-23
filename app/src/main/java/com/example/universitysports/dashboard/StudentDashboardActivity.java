package com.example.universitysports.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.auth.LoginActivity;
import com.example.universitysports.bookings.MyBookingsActivity;
import com.example.universitysports.events.EventsListActivity;
import com.example.universitysports.grounds.GroundListActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.SessionManager;

/**
 * StudentDashboardActivity - Main dashboard for students
 * Shows quick stats and navigation to all features
 */
public class StudentDashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvMyBookingsCount, tvEventsCount;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        initViews();
        initHelpers();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvMyBookingsCount = findViewById(R.id.tvMyBookingsCount);
        tvEventsCount = findViewById(R.id.tvEventsCount);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void setupUI() {
        // Display user name
        String userName = sessionManager.getUser().getName();
        tvUserName.setText(userName != null ? userName : "Student");

        // Update counts
        updateCounts();
    }

    private void updateCounts() {
        int userId = sessionManager.getUserId();
        int bookingsCount = dbHelper.getUserBookings(userId).size();
        int eventsCount = dbHelper.getUpcomingEvents().size();
        
        tvMyBookingsCount.setText(String.valueOf(bookingsCount));
        tvEventsCount.setText(String.valueOf(eventsCount));
    }

    private void setupListeners() {
        // Book Ground
        findViewById(R.id.cardBookGround).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentDashboardActivity.this, GroundListActivity.class));
            }
        });

        // My Bookings
        findViewById(R.id.cardMyBookings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentDashboardActivity.this, MyBookingsActivity.class));
            }
        });

        // Events
        findViewById(R.id.cardEvents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentDashboardActivity.this, EventsListActivity.class));
            }
        });

        // Profile (for now just show info)
        findViewById(R.id.cardProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StudentDashboardActivity.this, 
                    "Role: Student\nEmail: " + sessionManager.getUser().getEmail(), 
                    Toast.LENGTH_LONG).show();
            }
        });

        // Logout
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh counts when returning to dashboard
        updateCounts();
    }
}