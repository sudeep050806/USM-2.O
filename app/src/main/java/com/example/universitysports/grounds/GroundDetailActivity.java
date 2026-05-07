package com.example.universitysports.grounds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.bookings.BookGroundActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.models.Ground;

/**
 * GroundDetailActivity - Shows ground details, maintenance status, and booking option.
 */
public class GroundDetailActivity extends AppCompatActivity {

    private TextView tvGroundName, tvSportType, tvDescription, tvCapacity, tvTiming, tvLocation;
    private LinearLayout layoutMaintenanceBanner;
    private TextView tvMaintenanceReason, tvMaintenanceDate;
    private DBHelper dbHelper;
    private Ground currentGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground_detail);

        initViews();
        initHelpers();
        loadGroundDetails();
        setupBookButton();
    }

    private void initViews() {
        tvGroundName = findViewById(R.id.tvGroundName);
        tvSportType = findViewById(R.id.tvSportType);
        tvDescription = findViewById(R.id.tvDescription);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvTiming = findViewById(R.id.tvTiming);
        tvLocation = findViewById(R.id.tvLocation);
        layoutMaintenanceBanner = findViewById(R.id.layoutMaintenanceBanner);
        tvMaintenanceReason = findViewById(R.id.tvMaintenanceReason);
        tvMaintenanceDate = findViewById(R.id.tvMaintenanceDate);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void loadGroundDetails() {
        int groundId = getIntent().getIntExtra("ground_id", -1);
        if (groundId == -1) {
            Toast.makeText(this, "Error loading ground", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentGround = dbHelper.getGroundById(groundId);
        if (currentGround != null) {
            tvGroundName.setText(currentGround.getName());
            tvSportType.setText(currentGround.getSportType());
            tvDescription.setText(currentGround.getDescription());
            tvCapacity.setText(String.valueOf(currentGround.getCapacity()));
            tvLocation.setText(currentGround.getLocation());

            String opening = formatTime(currentGround.getAvailableFrom());
            String closing = formatTime(currentGround.getAvailableTo());
            tvTiming.setText(opening + " - " + closing);

            // Show maintenance banner if applicable
            if (currentGround.isUnderMaintenance()) {
                layoutMaintenanceBanner.setVisibility(View.VISIBLE);
                tvMaintenanceReason.setText("Reason: " + currentGround.getMaintenanceReason());
                String expectedDate = currentGround.getMaintenanceExpectedDate();
                tvMaintenanceDate.setText("Expected Completion: " +
                        (expectedDate != null && !expectedDate.isEmpty() ? expectedDate : "TBD"));
            } else {
                layoutMaintenanceBanner.setVisibility(View.GONE);
            }
        }
    }

    private void setupBookButton() {
        final android.widget.Button btnBookNow = findViewById(R.id.btnBookNow);
        btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGround == null) return;

                if (!currentGround.isActive()) {
                    Toast.makeText(GroundDetailActivity.this,
                        "Ground not available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (currentGround.isUnderMaintenance()) {
                    Toast.makeText(GroundDetailActivity.this,
                        "🔧 This ground is currently under maintenance and cannot be booked.",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(GroundDetailActivity.this, BookGroundActivity.class);
                intent.putExtra("ground_id", currentGround.getId());
                intent.putExtra("ground_name", currentGround.getName());
                startActivity(intent);
            }
        });

        // Update button appearance if under maintenance
        if (currentGround != null && currentGround.isUnderMaintenance()) {
            btnBookNow.setEnabled(false);
            btnBookNow.setText("Under Maintenance – Unavailable");
            btnBookNow.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#BDBDBD")));
        }
    }

    private String formatTime(String time24) {
        if (time24 == null || time24.isEmpty()) return time24;
        String[] parts = time24.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String period = hour >= 12 ? "PM" : "AM";
        hour = hour % 12;
        if (hour == 0) hour = 12;
        return String.format("%d:%02d %s", hour, minute, period);
    }
}