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
 * TeacherDashboardActivity - Dashboard for staff members
 * Similar to student but with potentially different permissions
 */
public class TeacherDashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvMyBookingsCount, tvEventsCount;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard); // Reuse same layout

        setTitle("Teacher Dashboard");
        
        initViews();
        initHelpers();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvMyBookingsCount = findViewById(R.id.tvMyBookingsCount);
        tvEventsCount = findViewById(R.id.tvEventsCount);
        findViewById(R.id.toolbar).setTitle("Teacher Dashboard");
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void setupUI() {
        String userName = sessionManager.getUser().getName();
        tvUserName.setText(userName != null ? userName : "Teacher");
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
        findViewById(R.id.cardBookGround).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherDashboardActivity.this, GroundListActivity.class));
            }
        });

        findViewById(R.id.cardMyBookings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherDashboardActivity.this, MyBookingsActivity.class));
            }
        });

        findViewById(R.id.cardEvents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherDashboardActivity.this, EventsListActivity.class));
            }
        });

        findViewById(R.id.cardProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TeacherDashboardActivity.this, 
                    "Role: Teacher/Staff\nEmail: " + sessionManager.getUser().getEmail(), 
                    Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounts();
    }
}