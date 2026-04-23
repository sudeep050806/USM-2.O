package com.example.universitysports.dashboard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.auth.LoginActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.SessionManager;

/**
 * AdminReportsActivity - Shows facility usage statistics
 */
public class AdminReportsActivity extends AppCompatActivity {

    private TextView tvRevenue;
    private SessionManager sessionManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        initViews();
        initHelpers();
        loadStats();
    }

    private void initViews() {
        tvRevenue = findViewById(R.id.tvRevenue);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
    }

    private void loadStats() {
        // Calculate revenue (assuming fixed price per booking)
        int totalBookings = dbHelper.getTotalBookings();
        int revenuePerBooking = 100; // Can be dynamic based on ground type
        int totalRevenue = totalBookings * revenuePerBooking;

        tvRevenue.setText("₹ " + totalRevenue);
        
        // In a real app, you would also show:
        // - Ground utilization percentages
        // - Popular time slots
        // - User growth trends
        // - Event attendance stats
    }
}