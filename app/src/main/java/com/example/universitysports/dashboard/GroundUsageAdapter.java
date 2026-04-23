package com.example.universitysports.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.models.Ground;

import java.util.List;

/**
 * GroundUsageAdapter - Shows ground usage statistics
 */
public class GroundUsageAdapter extends RecyclerView.Adapter<GroundUsageAdapter.UsageViewHolder> {

    private Context context;
    private List<Ground> groundList;

    public GroundUsageAdapter(Context context) {
        this.context = context;
        this.groundList = new java.util.ArrayList<>();
    }

    @NonNull
    @Override
    public UsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ground_usage, parent, false);
        return new UsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageViewHolder holder, int position) {
        final Ground ground = groundList.get(position);

        holder.tvGroundName.setText(ground.getName());
        holder.tvBookingCount.setText(ground.getBookingCount() + " bookings this month");

        // Calculate usage percentage (max 100%)
        int maxExpectedBookings = 30; // Max bookings per month assumed
        int usagePercent = (int) (((float) ground.getBookingCount() / maxExpectedBookings) * 100);
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

        com.example.universitysports.helpers.DBHelper dbHelper = new com.example.universitysports.helpers.DBHelper(context);
        List<Ground> allGrounds = dbHelper.getAllGrounds();
        for (Ground ground : allGrounds) {
            int count = dbHelper.getBookingCountForGround(ground.getId());
            ground.setBookingCount(count);
            groundList.add(ground);
        }

        notifyDataSetChanged();
    }

    public static class UsageViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroundName, tvBookingCount, tvUsagePercent;
        ProgressBar progressUsage;

        public UsageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroundName = itemView.findViewById(R.id.tvGroundName);
            tvBookingCount = itemView.findViewById(R.id.tvBookingCount);
            tvUsagePercent = itemView.findViewById(R.id.tvUsagePercent);
            progressUsage = itemView.findViewById(R.id.progressUsage);
        }
    }
}