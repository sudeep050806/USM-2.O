package com.example.universitysports.bookings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;

import java.util.List;

/**
 * TimeSlotAdapter - Displays available time slots for booking
 */
public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    private Context context;
    private List<TimeSlot> slotList;
    private OnSlotSelectedListener listener;

    public interface OnSlotSelectedListener {
        void onSlotSelected(String slotStartTime);
    }

    public TimeSlotAdapter(Context context, List<TimeSlot> slotList, OnSlotSelectedListener listener) {
        this.context = context;
        this.slotList = slotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        final TimeSlot slot = slotList.get(position);

        holder.tvTimeSlot.setText(slot.getTimeRange());
        holder.tvSlotStatus.setText(slot.isAvailable() ? "Available" : "Booked");

        // Set background and enabled state based on availability
        if (slot.isAvailable()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_slot_available);
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_slot_booked);
            holder.itemView.setEnabled(false);
            holder.itemView.setAlpha(0.5f);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slot.isAvailable()) {
                    listener.onSlotSelected(slot.getSlotStartTime());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    /**
     * TimeSlot model-class
     */
    public static class TimeSlot {
        private String timeRange;
        private boolean available;
        private String slotStartTime;

        public TimeSlot(String timeRange, boolean available, String slotStartTime) {
            this.timeRange = timeRange;
            this.available = available;
            this.slotStartTime = slotStartTime;
        }

        public String getTimeRange() {
            return timeRange;
        }

        public boolean isAvailable() {
            return available;
        }

        public String getSlotStartTime() {
            return slotStartTime;
        }
    }

    /**
     * ViewHolder for time slot items
     */
    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimeSlot, tvSlotStatus;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTimeSlot = itemView.findViewById(R.id.tvTimeSlot);
            tvSlotStatus = itemView.findViewById(R.id.tvSlotStatus);
        }
    }
}