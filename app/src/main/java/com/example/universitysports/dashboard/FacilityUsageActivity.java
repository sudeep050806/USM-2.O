package com.example.universitysports.dashboard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.adapters.GroundUsageAdapter;

/**
 * FacilityUsageActivity - Shows usage statistics for admin
 */
public class FacilityUsageActivity extends AppCompatActivity {

    private RecyclerView rvGroundUsage;
    private GroundUsageAdapter adapter;
    private TextView tvTotalBookings, tvActiveGrounds, tvTotalRevenue;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_usage);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadStats();
    }

    private void initViews() {
        rvGroundUsage = findViewById(R.id.rvGroundUsage);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvActiveGrounds = findViewById(R.id.tvActiveGrounds);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        adapter = new GroundUsageAdapter(this);
        rvGroundUsage.setLayoutManager(new LinearLayoutManager(this));
        rvGroundUsage.setAdapter(adapter);
    }

    /**
     * Load statistics data
     */
    private void loadStats() {
        // Overall stats
        int totalBookings = dbHelper.getTotalBookings();
        int activeGrounds = dbHelper.getActiveGroundsCount();

        tvTotalBookings.setText(String.valueOf(totalBookings));
        tvActiveGrounds.setText(String.valueOf(activeGrounds));

        // Mock revenue (in production, track payment status)
        tvTotalRevenue.setText("₹ " + (totalBookings * 100)); // Assuming Rs.100 per booking

        // Ground-wise usage
        adapter.loadGroundUsage();
    }

    /**
     * Inner class - Ground Usage Adapter
     */
    public class GroundUsageAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<GroundUsageAdapter.UsageViewHolder> {

        private android.content.Context context;
        private java.util.List<com.example.universitysports.models.Ground> groundList;

        public GroundUsageAdapter(android.content.Context context) {
            this.context = context;
            this.groundList = new java.util.ArrayList<>();
        }

        @Override
        public UsageViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_ground_usage, parent, false);
            return new UsageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UsageViewHolder holder, int position) {
            final com.example.universitysports.models.Ground ground = groundList.get(position);

            holder.tvGroundName.setText(ground.getName());
            holder.tvBookingCount.setText(ground.getBookingCount() + " bookings this month");

            // Set progress
            int maxCapacity = 100;
            int usagePercent = (int) (((float) ground.getBookingCount() / 30) * maxCapacity);
            holder.progressUsage.setProgress(Math.min(usagePercent, 100));
            holder.tvUsagePercent.setText(usagePercent + "%");
        }

        @Override
        public int getItemCount() {
            return groundList.size();
        }

        /**
         * Load ground usage data from DB
         */
        public void loadGroundUsage() {
            groundList.clear();

            // Get all grounds and count bookings
            java.util.List<com.example.universitysports.models.Ground> allGrounds = dbHelper.getAllGrounds();
            for (com.example.universitysports.models.Ground ground : allGrounds) {
                // Count bookings for this ground (this month)
                int count = dbHelper.getBookingCountForGround(ground.getId());
                ground.setBookingCount(count);
                groundList.add(ground);
            }

            notifyDataSetChanged();
        }

        public static class UsageViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView tvGroundName, tvBookingCount, tvUsagePercent;
            ProgressBar progressUsage;

            public UsageViewHolder(android.view.View itemView) {
                super(itemView);
                tvGroundName = itemView.findViewById(R.id.tvGroundName);
                tvBookingCount = itemView.findViewById(R.id.tvBookingCount);
                tvUsagePercent = itemView.findViewById(R.id.tvUsagePercent);
                progressUsage = itemView.findViewById(R.id.progressUsage);
            }
        }
    }
}