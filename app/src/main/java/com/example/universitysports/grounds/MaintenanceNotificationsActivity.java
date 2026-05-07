package com.example.universitysports.grounds;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.models.Ground;

import java.util.List;

/**
 * MaintenanceNotificationsActivity - Displays grounds currently under maintenance.
 */
public class MaintenanceNotificationsActivity extends AppCompatActivity {

    private RecyclerView rvMaintenanceNotifications;
    private TextView tvNoMaintenance;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_notifications);

        initViews();
        initHelpers();
        loadMaintenanceGrounds();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvMaintenanceNotifications = findViewById(R.id.rvMaintenanceNotifications);
        tvNoMaintenance = findViewById(R.id.tvNoMaintenance);
        rvMaintenanceNotifications.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void loadMaintenanceGrounds() {
        List<Ground> grounds = dbHelper.getMaintenanceGrounds();
        
        if (grounds.isEmpty()) {
            tvNoMaintenance.setVisibility(View.VISIBLE);
            rvMaintenanceNotifications.setVisibility(View.GONE);
        } else {
            tvNoMaintenance.setVisibility(View.GONE);
            rvMaintenanceNotifications.setVisibility(View.VISIBLE);
            MaintenanceNotificationAdapter adapter = new MaintenanceNotificationAdapter(grounds);
            rvMaintenanceNotifications.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Adapter for maintenance notifications
     */
    private static class MaintenanceNotificationAdapter extends RecyclerView.Adapter<MaintenanceNotificationAdapter.ViewHolder> {
        private final List<Ground> grounds;

        public MaintenanceNotificationAdapter(List<Ground> grounds) {
            this.grounds = grounds;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_maintenance_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Ground ground = grounds.get(position);
            holder.tvGroundName.setText(ground.getName());
            holder.tvSportType.setText(ground.getSportType());
            holder.tvMaintenanceReason.setText(ground.getMaintenanceReason());
            holder.tvExpectedDate.setText(ground.getMaintenanceExpectedDate());
        }

        @Override
        public int getItemCount() {
            return grounds.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvGroundName, tvSportType, tvMaintenanceReason, tvExpectedDate;

            public ViewHolder(View itemView) {
                super(itemView);
                tvGroundName = itemView.findViewById(R.id.tvGroundName);
                tvSportType = itemView.findViewById(R.id.tvSportType);
                tvMaintenanceReason = itemView.findViewById(R.id.tvMaintenanceReason);
                tvExpectedDate = itemView.findViewById(R.id.tvExpectedDate);
            }
        }
    }
}
