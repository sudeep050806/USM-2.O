package com.example.universitysports.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.auth.LoginActivity;
import com.example.universitysports.bookings.MyBookingsActivity;
import com.example.universitysports.events.UpcomingEventsActivity;
import com.example.universitysports.grounds.GroundListActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.SessionManager;

/**
 * User Dashboard - Main screen after login for regular users
 */
public class UserDashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvMyBookings;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Initialize
        initViews();
        initHelpers();

        // Setup UI
        setupUI();

        // Setup listeners
        setupListeners();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvMyBookings = findViewById(R.id.tvMyBookings);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void setupUI() {
        // Display user name
        String userName = sessionManager.getUser().getName();
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        }

        // Update bookings count
        int bookingsCount = dbHelper.getUserBookings(sessionManager.getUserId()).size();
        tvMyBookings.setText(String.valueOf(bookingsCount));
    }

    private void setupListeners() {
        // Book Ground card click
        findViewById(R.id.cardBookGround).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                startActivity(new Intent(UserDashboardActivity.this, GroundListActivity.class));
            }
        });

        // My Bookings card click
        findViewById(R.id.cardMyBookings).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                startActivity(new Intent(UserDashboardActivity.this, MyBookingsActivity.class));
            }
        });

        // Events card click
        findViewById(R.id.cardEvents).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                startActivity(new Intent(UserDashboardActivity.this, UpcomingEventsActivity.class));
            }
        });

        // Logout card click
        findViewById(R.id.cardLogout).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                logout();
            }
        });

        // Bottom navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(new com.google.android.material.bottomnavigation.BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(android.view.MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_bookings) {
                    startActivity(new Intent(UserDashboardActivity.this, MyBookingsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Handle user logout
     */
    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UserDashboardActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bookings count when returning to dashboard
        int bookingsCount = dbHelper.getUserBookings(sessionManager.getUserId()).size();
        tvMyBookings.setText(String.valueOf(bookingsCount));
    }
}