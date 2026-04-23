package com.example.universitysports.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.admin.AdminReportsActivity;
import com.example.universitysports.auth.LoginActivity;
import com.example.universitysports.bookings.ManageBookingsActivity;
import com.example.universitysports.events.ManageEventsActivity;
import com.example.universitysports.grounds.ManageGroundsActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.SessionManager;

/**
 * AdminDashboardActivity - Main admin panel with statistics and management tools
 */
public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalBookings, tvActiveGrounds, tvUpcomingEvents, tvTotalUsers;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        initHelpers();
        loadStats();
        setupListeners();
    }

    private void initViews() {
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvActiveGrounds = findViewById(R.id.tvActiveGrounds);
        tvUpcomingEvents = findViewById(R.id.tvUpcomingEvents);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void loadStats() {
        // Load statistics from database
        int totalBookings = dbHelper.getTotalBookings();
        int activeGrounds = dbHelper.getActiveGroundsCount();
        int upcomingEvents = dbHelper.getUpcomingEventsCount();
        int totalUsers = dbHelper.getUsersByRole("student").size() + 
                        dbHelper.getUsersByRole("teacher").size();

        tvTotalBookings.setText(String.valueOf(totalBookings));
        tvActiveGrounds.setText(String.valueOf(activeGrounds));
        tvUpcomingEvents.setText(String.valueOf(upcomingEvents));
        tvTotalUsers.setText(String.valueOf(totalUsers));
    }

    private void setupListeners() {
        findViewById(R.id.cardManageGrounds).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageGroundsActivity.class));
            }
        });

        findViewById(R.id.cardManageBookings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageBookingsActivity.class));
            }
        });

        findViewById(R.id.cardManageEvents).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageEventsActivity.class));
            }
        });

        findViewById(R.id.cardReports).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, AdminReportsActivity.class));
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
        // Refresh stats when returning to dashboard
        loadStats();
    }
}