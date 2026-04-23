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
import com.example.universitysports.auth.LoginActivity;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.helpers.EmailSender;
import com.example.universitysports.helpers.SessionManager;
import com.example.universitysports.models.Booking;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * MyBookingsActivity - View and cancel user's bookings
 */
public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvBookings;
    private TextView tvEmpty, tvTotal;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private DBHelper dbHelper;
    private EmailSender emailSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadBookings();
    }

    private void initViews() {
        rvBookings = findViewById(R.id.rvBookings);
        tvEmpty = findViewById(R.id.tvEmptyBookings);
        tvTotal = findViewById(R.id.tvTotalBookings);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initHelpers() {
        sessionManager = new SessionManager(this);
        dbHelper = new DBHelper(this);
        emailSender = new EmailSender();
    }

    private void setupRecyclerView() {
        BookingAdapter adapter = new BookingAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(adapter);
    }

    private void loadBookings() {
        int userId = sessionManager.getUserId();
        List<Booking> bookings = dbHelper.getUserBookings(userId);

        if (bookings.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvBookings.setVisibility(View.GONE);
            tvTotal.setText("Total: 0");
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvBookings.setVisibility(View.VISIBLE);
            tvTotal.setText("Total: " + bookings.size());
            
            // Update adapter
            BookingAdapter adapter = (BookingAdapter) rvBookings.getAdapter();
            if (adapter != null) {
                adapter.updateData(bookings);
            }
        }
    }

    /**
     * Cancel a booking
     */
    void cancelBooking(final Booking booking) {
        progressBar.setVisibility(View.VISIBLE);
        
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = dbHelper.cancelBooking(booking.getId(), sessionManager.getUserId());
                
                progressBar.setVisibility(View.GONE);
                
                if (success) {
                    // Send cancellation email
                    emailSender.sendCancellationEmail(
                        sessionManager.getUser().getEmail(),
                        booking.getGroundName(),
                        booking.getBookingDate(),
                        booking.getTimeSlot()
                    );
                    
                    Toast.makeText(MyBookingsActivity.this, 
                        "Booking cancelled", Toast.LENGTH_SHORT).show();
                    loadBookings();
                } else {
                    Toast.makeText(MyBookingsActivity.this, 
                        "Failed to cancel", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }

    /**
     * Booking Adapter inner class
     */
    public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

        private List<Booking> bookingList;

        public BookingAdapter(android.content.Context context) {
            this.bookingList = new ArrayList<>();
        }

        @Override
        public BookingViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_booking, parent, false);
            return new BookingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BookingViewHolder holder, int position) {
            final Booking booking = bookingList.get(position);

            holder.tvGroundName.setText(booking.getGroundName());
            holder.tvStatus.setText(booking.getStatus());
            
            // Set status color
            int colorRes = getStatusColor(booking.getStatus());
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(colorRes));
            
            // Format date & time
            holder.tvDate.setText(formatDate(booking.getBookingDate()));
            holder.tvTime.setText(booking.getTimeSlot());
            holder.tvBookingId.setText("ID: BK" + booking.getId());

            // Show cancel button only for pending/confirmed
            if ("Pending".equalsIgnoreCase(booking.getStatus()) || 
                "Confirmed".equalsIgnoreCase(booking.getStatus())) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelBooking(booking);
                    }
                });
            } else {
                holder.btnCancel.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return bookingList.size();
        }

        public void updateData(List<Booking> newList) {
            this.bookingList = newList;
            notifyDataSetChanged();
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
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = input.parse(dateStr);
                return output.format(date);
            } catch (Exception e) {
                return dateStr;
            }
        }

        public static class BookingViewHolder extends RecyclerView.ViewHolder {
            TextView tvGroundName, tvStatus, tvDate, tvTime, tvBookingId;
            com.google.android.material.button.MaterialButton btnCancel;

            public BookingViewHolder(android.view.View itemView) {
                super(itemView);
                tvGroundName = itemView.findViewById(R.id.tvGroundName);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvBookingId = itemView.findViewById(R.id.tvBookingId);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}