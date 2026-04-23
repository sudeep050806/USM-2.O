package com.example.universitysports.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.dashboard.StudentDashboardActivity;
import com.example.universitysports.dashboard.TeacherDashboardActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.helpers.SessionManager;
import com.example.universitysports.models.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * BookGroundActivity - Select date & time slot for booking
 */
public class BookGroundActivity extends AppCompatActivity implements TimeSlotAdapter.OnSlotSelectedListener {

    private TextView tvSelectedGround, tvSelectedDate;
    private RecyclerView rvTimeSlots;
    private ProgressBar progressBar;
    
    private int groundId;
    private String groundName;
    private String selectedDate;
    private String selectedTimeSlot;
    private TimeSlotAdapter timeSlotAdapter;
    
    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ground);

        initViews();
        initHelpers();
        getIntentData();
        setupUI();
        setupListeners();
    }

    private void initViews() {
        tvSelectedGround = findViewById(R.id.tvSelectedGround);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        rvTimeSlots = findViewById(R.id.rvTimeSlots);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void getIntentData() {
        groundId = getIntent().getIntExtra("ground_id", -1);
        groundName = getIntent().getStringExtra("ground_name");
        if (groundId == -1) {
            Toast.makeText(this, "Invalid ground", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupUI() {
        tvSelectedGround.setText(groundName);
        rvTimeSlots.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        findViewById(R.id.btnShowSlots).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = getTodayDate();
                tvSelectedDate.setText("Selected: " + formatDate(selectedDate));
                loadAvailableSlots();
            }
        });

        findViewById(R.id.btnConfirmBooking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTimeSlot != null) {
                    confirmBooking();
                } else {
                    Toast.makeText(BookGroundActivity.this, 
                        "Please select a time slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Load available time slots for selected date
     */
    private void loadAvailableSlots() {
        List<TimeSlotAdapter.TimeSlot> slots = new ArrayList<>();
        
        for (String slotStart : com.example.universitysports.helpers.AppConfig.TIME_SLOTS) {
            String endTime = getEndTime(slotStart);
            boolean isAvailable = dbHelper.isSlotAvailable(groundId, selectedDate, slotStart + " - " + endTime);
            slots.add(new TimeSlotAdapter.TimeSlot(
                slotStart + " - " + endTime,
                isAvailable,
                slotStart
            ));
        }
        
        timeSlotAdapter = new TimeSlotAdapter(this, slots, this);
        rvTimeSlots.setAdapter(timeSlotAdapter);
    }

    @Override
    public void onSlotSelected(String slotStartTime) {
        selectedTimeSlot = slotStartTime;
        findViewById(R.id.btnConfirmBooking).setEnabled(true);
    }

    /**
     * Confirm booking
     */
    private void confirmBooking() {
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnConfirmBooking).setEnabled(false);

        findViewById(R.id.btnConfirmBooking).postDelayed(new Runnable() {
            @Override
            public void run() {
                Booking booking = new Booking();
                booking.setUserId(sessionManager.getUserId());
                booking.setGroundId(groundId);
                booking.setBookingDate(selectedDate);
                booking.setTimeSlot(selectedTimeSlot + " - " + getEndTime(selectedTimeSlot));
                booking.setGroundName(groundName);
                booking.setUserName(sessionManager.getUser().getName());

                long bookingId = dbHelper.createBooking(booking);

                if (bookingId > 0) {
                    // Send confirmation email
                    String email = sessionManager.getUser().getEmail();
                    emailSender.sendBookingConfirmation(email, groundName, 
                        selectedDate, booking.getTimeSlot(), String.valueOf(bookingId));

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(BookGroundActivity.this, 
                        "Booking confirmed!", Toast.LENGTH_LONG).show();
                    
                    setResult(RESULT_OK);
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    findViewById(R.id.btnConfirmBooking).setEnabled(true);
                    Toast.makeText(BookGroundActivity.this, 
                        "Booking failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1500);
    }

    private String getEndTime(String startTime) {
        String[] parts = startTime.split(":");
        int hour = Integer.parseInt(parts[0]);
        return String.format("%02d:%s", (hour + 1) % 24, parts[1]);
    }

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = input.parse(dateStr);
            return output.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}