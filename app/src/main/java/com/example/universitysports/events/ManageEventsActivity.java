package com.example.universitysports.events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.models.Event;
import com.google.android.material.textfield.TextInputEditText;

/**
 * ManageEventsActivity - Admin interface for managing sports events
 */
public class ManageEventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventManagementAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadEvents();
        
        findViewById(R.id.fabAddEvent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog(null);
            }
        });
    }

    private void initViews() {
        rvEvents = findViewById(R.id.rvEvents);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        adapter = new EventManagementAdapter(this);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(adapter);
    }

    private void loadEvents() {
        java.util.List<Event> events = dbHelper.getUpcomingEvents();
        adapter.updateData(events);
    }

    /**
     * Show add/edit event dialog
     */
    private void showAddEventDialog(final Event existingEvent) {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_event, null);
        
        final TextInputEditText etName = dialogView.findViewById(R.id.etEventName);
        final TextInputEditText etDate = dialogView.findViewById(R.id.etDate);
        final TextInputEditText etTime = dialogView.findViewById(R.id.etTime);
        final TextInputEditText etVenue = dialogView.findViewById(R.id.etVenue);
        final TextInputEditText etOrganizer = dialogView.findViewById(R.id.etOrganizer);
        final TextInputEditText etMax = dialogView.findViewById(R.id.etMaxParticipants);
        final TextInputEditText etDesc = dialogView.findViewById(R.id.etDescription);

        if (existingEvent != null) {
            etName.setText(existingEvent.getTitle());
            etDate.setText(existingEvent.getEventDate());
            etTime.setText(existingEvent.getEventTime());
            etVenue.setText(existingEvent.getVenue());
            etOrganizer.setText(existingEvent.getOrganizer());
            etMax.setText(String.valueOf(existingEvent.getMaxParticipants()));
            etDesc.setText(existingEvent.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existingEvent == null ? "Add New Event" : "Edit Event")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent(dialog, existingEvent,
                        etName.getText().toString().trim(),
                        etDate.getText().toString().trim(),
                        etTime.getText().toString().trim(),
                        etVenue.getText().toString().trim(),
                        etOrganizer.getText().toString().trim(),
                        etMax.getText().toString().trim(),
                        etDesc.getText().toString().trim());
            }
        });
    }

    /**
     * Save event to database
     */
    private void saveEvent(final AlertDialog dialog, final Event existingEvent,
                           String name, String date, String time, String venue,
                           String organizer, String maxStr, String desc) {

        if (name.isEmpty() || date.isEmpty() || time.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int max;
        try {
            max = Integer.parseInt(maxStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid max participants", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = existingEvent != null ? existingEvent : new Event();
        event.setTitle(name);
        event.setEventDate(date);
        event.setEventTime(time);
        event.setVenue(venue);
        event.setOrganizer(organizer);
        event.setMaxParticipants(max);
        event.setDescription(desc);
        event.setActive(true);

        boolean success;
        if (existingEvent != null) {
            success = dbHelper.updateEvent(event);
        } else {
            long id = dbHelper.createEvent(event);
            success = id > 0;
        }

        if (success) {
            Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadEvents();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Confirm delete event
     */
    void confirmDelete(final Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Delete " + event.getTitle() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.deleteEvent(event.getId())) {
                            Toast.makeText(ManageEventsActivity.this, 
                                "Event deleted", Toast.LENGTH_SHORT).show();
                            loadEvents();
                        } else {
                            Toast.makeText(ManageEventsActivity.this, 
                                "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Event Management Adapter
     */
    public class EventManagementAdapter extends RecyclerView.Adapter<EventManagementAdapter.EventViewHolder> {

        private java.util.List<Event> eventList;
        private android.content.Context context;

        public EventManagementAdapter(android.content.Context context) {
            this.context = context;
            this.eventList = new java.util.ArrayList<>();
        }

        @Override
        public EventViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_manage_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventViewHolder holder, final int position) {
            final Event event = eventList.get(position);

            holder.tvEventName.setText(event.getTitle());
            holder.tvEventDate.setText(formatDate(event.getEventDate()));
            holder.tvVenue.setText(event.getVenue());

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddEventDialog(event);
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelete(event);
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

        public static class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventName, tvEventDate, tvVenue;
            Button btnEdit, btnDelete;

            public EventViewHolder(android.view.View itemView) {
                super(itemView);
                tvEventName = itemView.findViewById(R.id.tvEventName);
                tvEventDate = itemView.findViewById(R.id.tvEventDate);
                tvVenue = itemView.findViewById(R.id.tvVenue);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}