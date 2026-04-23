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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * EventsListActivity - Display upcoming sports events
 */
public class EventsListActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);
        
        initViews();
        initHelpers();
        setupRecyclerView();
        loadEvents();
    }

    private void initViews() {
        rvEvents = findViewById(R.id.rvEvents);
        tvEmpty = findViewById(R.id.tvEmptyEvents);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void setupRecyclerView() {
        EventAdapter adapter = new EventAdapter(this);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);
    }

    private void loadEvents() {
        List<Event> events = dbHelper.getUpcomingEvents();
        
        if (events.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvEvents.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvEvents.setVisibility(View.VISIBLE);
            EventAdapter adapter = (EventAdapter) rvEvents.getAdapter();
            if (adapter != null) {
                adapter.updateData(events);
            }
        }
    }

    /**
     * Event Adapter inner class
     */
    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

        private List<Event> eventList;

        public EventAdapter(android.content.Context context) {
            this.eventList = new ArrayList<>();
        }

        @Override
        public EventViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventViewHolder holder, int position) {
            final Event event = eventList.get(position);

            holder.tvEventName.setText(event.getTitle());
            holder.tvDate.setText(formatDate(event.getEventDate()));
            holder.tvTime.setText(formatTime(event.getEventTime()));
            holder.tvVenue.setText(event.getVenue());
            holder.tvOrganizer.setText(event.getOrganizer());
            
            String regText = event.getRegisteredCount() + "/" + event.getMaxParticipants();
            holder.tvRegisteredCount.setText(regText);
            
            int progress = (int) (((float) event.getRegisteredCount() / event.getMaxParticipants()) * 100);
            holder.progressRegister.setProgress(progress);

            // Check if event is full
            if (event.getRegisteredCount() >= event.getMaxParticipants()) {
                holder.btnRegister.setEnabled(false);
                holder.btnRegister.setText("FULL");
            } else {
                holder.btnRegister.setEnabled(true);
                holder.btnRegister.setText("Register");
                
                holder.btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerForEvent(event);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public void updateData(List<Event> newList) {
            this.eventList = newList;
            notifyDataSetChanged();
        }

        private void registerForEvent(final Event event) {
            progressBar.setVisibility(View.VISIBLE);
            
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    event.setRegisteredCount(event.getRegisteredCount() + 1);
                    dbHelper.updateEvent(event);
                    
                    emailSender.sendEventRegistrationEmail(
                        sessionManager.getUser().getEmail(),
                        event.getTitle(),
                        event.getEventDate(),
                        event.getEventTime()
                    );
                    
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EventsListActivity.this, 
                        "Successfully registered!", Toast.LENGTH_LONG).show();
                    loadEvents();
                }
            }, 1000);
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

        public static class EventViewHolder extends RecyclerView.ViewHolder {
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