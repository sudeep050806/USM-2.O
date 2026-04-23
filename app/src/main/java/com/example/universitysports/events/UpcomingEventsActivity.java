package com.example.universitysports.events;

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
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.helpers.SessionManager;
import com.example.universitysports.models.Event;

import java.util.List;

/**
 * UpcomingEventsActivity - Displays list of upcoming sports events
 */
public class UpcomingEventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private TextView tvEmptyEvents;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadEvents();
    }

    private void initViews() {
        rvEvents = findViewById(R.id.rvEvents);
        tvEmptyEvents = findViewById(R.id.tvEmptyEvents);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void setupRecyclerView() {
        eventAdapter = new EventAdapter(this, new java.util.ArrayList<>());
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);
    }

    /**
     * Load upcoming events from database
     */
    private void loadEvents() {
        List<Event> events = dbHelper.getUpcomingEvents();

        if (events.isEmpty()) {
            tvEmptyEvents.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        } else {
            tvEmptyEvents.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
            eventAdapter.updateData(events);
        }
    }

    /**
     * Inner class - Event Adapter
     */
    public class EventAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private java.util.List<Event> eventList;
        private android.content.Context context;

        public EventAdapter(android.content.Context context, java.util.List<Event> eventList) {
            this.context = context;
            this.eventList = eventList;
        }

        @Override
        public EventViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventViewHolder holder, int position) {
            final Event event = eventList.get(position);

            holder.tvEventName.setText(event.getEventName());

            // Format date
            String formattedDate = formatDate(event.getEventDate());
            holder.tvDate.setText(formattedDate);

            // Format time
            String formattedTime = formatTime(event.getEventTime());
            holder.tvTime.setText(formattedTime);

            holder.tvVenue.setText(event.getVenue());
            holder.tvOrganizer.setText(event.getOrganizer());

            // Registration count
            String regText = event.getRegisteredCount() + "/" + event.getMaxParticipants();
            holder.tvRegisteredCount.setText(regText);

            // Progress percentage
            int progress = (int) (((float) event.getRegisteredCount() / event.getMaxParticipants()) * 100);
            holder.progressRegister.setProgress(progress);

            // Check if event is full
            if (event.getRegisteredCount() >= event.getMaxParticipants()) {
                holder.btnRegister.setEnabled(false);
                holder.btnRegister.setText("FULL");
            } else {
                holder.btnRegister.setEnabled(true);
                holder.btnRegister.setText("Register Now");
            }

            holder.btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerForEvent(event);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public void updateData(java.util.List<Event> newList) {
            this.eventList = newList;
            notifyDataSetChanged();
        }

        /**
         * Register user for event
         */
        private void registerForEvent(final Event event) {
            progressBar.setVisibility(View.VISIBLE);

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Increment registration count
                    event.setRegisteredCount(event.getRegisteredCount() + 1);
                    dbHelper.updateEvent(event);

                    // Send confirmation email
                    String email = sessionManager.getUser().getEmail();
                    emailSender.sendEventRegistrationEmail(email, event.getEventName(),
                            event.getEventDate(), event.getEventTime());

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(context,
                            "Successfully registered for event!", Toast.LENGTH_LONG).show();

                    // Refresh list
                    loadEvents();
                }
            }, 1000);
        }

        /**
         * Convert 24h to 12h time format
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

        /**
         * Format date for display
         */
        private String formatDate(String dateStr) {
            try {
                java.text.SimpleDateFormat input = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
                java.util.Date date = input.parse(dateStr);
                return output.format(date);
            } catch (Exception e) {
                return dateStr;
            }
        }

        public static class EventViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView tvEventName, tvDate, tvTime, tvVenue, tvOrganizer, tvRegisteredCount;
            com.google.android.material.button.MaterialButton btnRegister;
            ProgressBar progressRegister;

            public EventViewHolder(android.view.View itemView) {
                super(itemView);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvVenue = itemView.findViewById(R.id.tvVenue);
                tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
                tvRegisteredCount = itemView.findViewById(R.id.tvRegisteredCount);
                btnRegister = itemView.findViewById(R.id.btnRegister);
                progressRegister = itemView.findViewById(R.id.progressRegister);
            }
        }
    }
}