package com.example.universitysports.grounds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.universitysports.R;
import com.example.universitysports.helpers.DBHelper;
import com.example.universitysports.models.Ground;
import com.google.android.material.textfield.TextInputEditText;

/**
 * ManageGroundsActivity - Admin interface for managing grounds
 * Allows adding, editing, and deleting grounds
 */
public class ManageGroundsActivity extends AppCompatActivity {

    private RecyclerView rvGrounds;
    private GroundManagementAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_grounds);

        initViews();
        initHelpers();
        setupRecyclerView();
        loadGrounds();
        
        // FAB for adding new ground
        findViewById(R.id.fabAddGround).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddGroundDialog(null);
            }
        });
    }

    private void initViews() {
        rvGrounds = findViewById(R.id.rvGrounds);
    }

    private void initHelpers() {
        dbHelper = new DBHelper(this);
    }

    private void setupRecyclerView() {
        adapter = new GroundManagementAdapter(this);
        rvGrounds.setLayoutManager(new LinearLayoutManager(this));
        rvGrounds.setAdapter(adapter);
    }

    private void loadGrounds() {
        java.util.List<Ground> grounds = dbHelper.getAllGrounds();
        adapter.updateData(grounds);
    }

    /**
     * Show dialog to add or edit ground
     */
    private void showAddGroundDialog(final Ground existingGround) {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ground, null);
        
        final TextInputEditText etName = dialogView.findViewById(R.id.etName);
        final TextInputEditText etLocation = dialogView.findViewById(R.id.etLocation);
        final TextInputEditText etSport = dialogView.findViewById(R.id.etSportType);
        final TextInputEditText etCapacity = dialogView.findViewById(R.id.etCapacity);
        final TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        final TextInputEditText etOpening = dialogView.findViewById(R.id.etOpening);
        final TextInputEditText etClosing = dialogView.findViewById(R.id.etClosing);

        if (existingGround != null) {
            etName.setText(existingGround.getName());
            etLocation.setText(existingGround.getLocation());
            etSport.setText(existingGround.getSportType());
            etCapacity.setText(String.valueOf(existingGround.getCapacity()));
            etDescription.setText(existingGround.getDescription());
            etOpening.setText(existingGround.getAvailableFrom());
            etClosing.setText(existingGround.getAvailableTo());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existingGround == null ? "Add New Ground" : "Edit Ground")
                .setView(dialogView)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGround(dialog, existingGround,
                        etName.getText().toString().trim(),
                        etLocation.getText().toString().trim(),
                        etSport.getText().toString().trim(),
                        etCapacity.getText().toString().trim(),
                        etDescription.getText().toString().trim(),
                        etOpening.getText().toString().trim(),
                        etClosing.getText().toString().trim());
            }
        });
    }

    /**
     * Save ground to database
     */
    private void saveGround(final AlertDialog dialog, final Ground existingGround,
                            String name, String location, String sport,
                            String capacityStr, String desc, String opening, String closing) {
        
        // Validation
        if (name.isEmpty()) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sport.isEmpty()) {
            Toast.makeText(this, "Sport type required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capacityStr.isEmpty()) {
            Toast.makeText(this, "Capacity required", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid capacity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (opening.isEmpty() || closing.isEmpty()) {
            Toast.makeText(this, "Opening/closing times required", Toast.LENGTH_SHORT).show();
            return;
        }

        Ground ground = existingGround != null ? existingGround : new Ground();
        ground.setName(name);
        ground.setLocation(location);
        ground.setSportType(sport);
        ground.setCapacity(capacity);
        ground.setDescription(desc);
        ground.setAvailableFrom(opening);
        ground.setAvailableTo(closing);
        ground.setActive(true);

        boolean success;
        if (existingGround != null) {
            success = dbHelper.updateGround(ground);
        } else {
            long id = dbHelper.addGround(ground);
            success = id > 0;
        }

        if (success) {
            Toast.makeText(this, "Ground saved!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadGrounds();
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Confirm and delete ground
     */
    void confirmDelete(final Ground ground) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ground")
                .setMessage("Delete " + ground.getName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dbHelper.deleteGround(ground.getId())) {
                            Toast.makeText(ManageGroundsActivity.this, 
                                "Ground deleted", Toast.LENGTH_SHORT).show();
                            loadGrounds();
                        } else {
                            Toast.makeText(ManageGroundsActivity.this, 
                                "Delete failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Inner class - Ground Management Adapter
     */
    public class GroundManagementAdapter extends RecyclerView.Adapter<GroundManagementAdapter.GroundViewHolder> {

        private java.util.List<Ground> groundList;

        public GroundManagementAdapter(android.content.Context context) {
            this.groundList = new java.util.ArrayList<>();
        }

        @Override
        public GroundViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_manage_ground, parent, false);
            return new GroundViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GroundViewHolder holder, final int position) {
            final Ground ground = groundList.get(position);

            holder.tvGroundName.setText(ground.getName());
            holder.tvSportType.setText(ground.getSportType());

            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddGroundDialog(ground);
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelete(ground);
                }
            });
        }

        @Override
        public int getItemCount() {
            return groundList.size();
        }

        public void updateData(java.util.List<Ground> newList) {
            this.groundList = newList;
            notifyDataSetChanged();
        }

        public static class GroundViewHolder extends RecyclerView.ViewHolder {
            TextView tvGroundName, tvSportType;
            Button btnEdit, btnDelete;

            public GroundViewHolder(android.view.View itemView) {
                super(itemView);
                tvGroundName = itemView.findViewById(R.id.tvGroundName);
                tvSportType = itemView.findViewById(R.id.tvSportType);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}