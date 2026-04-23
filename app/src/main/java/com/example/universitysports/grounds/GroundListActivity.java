package com.example.universitysports.grounds;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.universitysports.R;
import com.example.universitysports.bookings.BookGroundActivity;
import com.example.universitysports.helpers.DBHelper;

/**
 * GroundListActivity - Display all available sports grounds
 */
public class GroundListActivity extends AppCompatActivity {

    private androidx.recyclerview.widget.RecyclerView rvGrounds;
    private GroundAdapter groundAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ground_list);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadGrounds();
    }

    private void initViews() {
        rvGrounds = findViewById(R.id.rvGrounds);
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        groundAdapter = new GroundAdapter(this, new java.util.ArrayList<>());
        rvGrounds.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        rvGrounds.setAdapter(groundAdapter);
    }

    private void loadGrounds() {
        java.util.List<Ground> grounds = dbHelper.getAllGrounds();
        if (grounds.isEmpty()) {
            Toast.makeText(this, "No grounds available", Toast.LENGTH_SHORT).show();
        }
        groundAdapter.updateData(grounds);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGrounds();
    }
}