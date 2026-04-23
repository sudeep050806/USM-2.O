package com.example.universitysports.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.grounds.GroundDetailActivity;
import com.example.universitysports.models.Ground;

import java.util.List;

/**
 * GroundAdapter - RecyclerView adapter for displaying grounds list
 */
public class GroundAdapter extends RecyclerView.Adapter<GroundAdapter.GroundViewHolder> {

    private List<Ground> groundList;
    private Context context;

    public GroundAdapter(Context context, List<Ground> groundList) {
        this.context = context;
        this.groundList = groundList;
    }

    @NonNull
    @Override
    public GroundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ground, parent, false);
        return new GroundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroundViewHolder holder, int position) {
        Ground ground = groundList.get(position);

        holder.tvGroundName.setText(ground.getName());
        holder.tvSportType.setText(ground.getSportType());
        holder.tvCapacity.setText("Capacity: " + ground.getCapacity());
        holder.tvGroundDescription.setText(ground.getDescription());
        
        // Format times
        String opening = formatTime(ground.getAvailableFrom());
        String closing = formatTime(ground.getAvailableTo());
        holder.tvTiming.setText("Open: " + opening + " - " + closing);

        // Click to view details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroundDetailActivity.class);
                intent.putExtra("ground_id", ground.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groundList.size();
    }

    public void updateData(List<Ground> newList) {
        this.groundList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for ground items
     */
    public static class GroundViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroundName, tvSportType, tvCapacity, tvGroundDescription, tvTiming;

        public GroundViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroundName = itemView.findViewById(R.id.tvGroundName);
            tvSportType = itemView.findViewById(R.id.tvSportType);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvGroundDescription = itemView.findViewById(R.id.tvGroundDescription);
            tvTiming = itemView.findViewById(R.id.tvTiming);
        }
    }

    /**
     * Convert 24h to 12h format
     */
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