package com.example.universitysports.bookings;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.models.Booking;

/**
 * ManageBookingsActivity - Admin interface to manage all bookings
 */
public class ManageBookingsActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private ProgressBar progressBar;
    private BookingManagementAdapter adapter;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadBookings();
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void setupRecyclerView() {
        adapter = new BookingManagementAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void loadBookings() {
        java.util.List<Booking> bookings = dbHelper.getAllBookings();
        adapter.updateData(bookings);
    }

    /**
     * Booking Management Adapter
     */
    public class BookingManagementAdapter extends RecyclerView.Adapter<BookingManagementAdapter.BookingViewHolder> {

        private java.util.List<Booking> bookingList;
        private android.content.Context context;

        public BookingManagementAdapter(android.content.Context context) {
            this.context = context;
            this.bookingList = new java.util.ArrayList<>();
        }

        @Override
        public BookingViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(context)
                    .inflate(R.layout.item_manage_booking, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BookingViewHolder holder, int position) {
            final Booking booking = bookingList.get(position);

            holder.tvBookingId.setText("ID: BK" + booking.getId());
            holder.tvUserName.setText(booking.getUserName());
            holder.tvGroundName.setText(booking.getGroundName());
            holder.tvDateTime.setText(formatDate(booking.getBookingDate()) + " | " + booking.getTimeSlot());
            holder.tvStatus.setText(booking.getStatus());

            // Color status
            int colorRes = getStatusColor(booking.getStatus());
            holder.tvStatus.setTextColor(holder.itemView.getResources().getColor(colorRes));

            // Button states based on status
            if ("Pending".equalsIgnoreCase(booking.getStatus())) {
                holder.btnApprove.setEnabled(true);
                holder.btnCancel.setEnabled(true);
                holder.btnCancel.setText("Reject");
            } else if ("Confirmed".equalsIgnoreCase(booking.getStatus())) {
                holder.btnApprove.setEnabled(false);
                holder.btnCancel.setEnabled(true);
                holder.btnCancel.setText("Cancel");
            } else {
                holder.btnApprove.setEnabled(false);
                holder.btnCancel.setEnabled(false);
                holder.btnCancel.setText("Cancelled");
            }

            holder.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateBookingStatus(booking.getId(), "Confirmed");
                }
            });

            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateBookingStatus(booking.getId(), "Cancelled");
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookingList.size();
        }

        public void updateData(java.util.List<Booking> newList) {
            this.bookingList = newList;
            notifyDataSetChanged();
        }

        private void updateBookingStatus(final int bookingId, final String status) {
            progressBar.setVisibility(View.VISIBLE);
            
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean success = dbHelper.updateBookingStatus(bookingId, status);
                    progressBar.setVisibility(View.GONE);
                    
                    if (success) {
                        Toast.makeText(context, "Booking " + status.toLowerCase(), Toast.LENGTH_SHORT).show();
                        loadBookings();
                    } else {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);
        }

        private int getStatusColor(String status) {
            switch (status.toLowerCase()) {
                case "confirmed": return R.color.success;
                case "pending": return R.color.warning;
                case "cancelled": return R.color.error;
                default: return R.color.text_secondary;
            }
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

        public static class BookingViewHolder extends RecyclerView.ViewHolder {
            TextView tvBookingId, tvUserName, tvGroundName, tvDateTime, tvStatus;
            com.google.android.material.button.MaterialButton btnApprove, btnCancel;

            public BookingViewHolder(android.view.View itemView) {
                super(itemView);
                tvBookingId = itemView.findViewById(R.id.tvBookingId);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvGroundName = itemView.findViewById(R.id.tvGroundName);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnApprove = itemView.findViewById(R.id.btnApprove);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}