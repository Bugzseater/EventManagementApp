package com.example.eventmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button btnManageEvents, btnManageVendors, btnViewBookings, btnLogout;
    private TextView tvTotalEvents, tvTotalBookings;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Make the layout go under the status bar (matching your theme)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // Initialize Database Helper
        dbHelper = new DatabaseHelper(this);

        // Initialize Stat TextViews
        tvTotalEvents = findViewById(R.id.tevnts);
        tvTotalBookings = findViewById(R.id.tbooking);

        // Initialize Buttons
        btnManageEvents = findViewById(R.id.btnManageEvents);
        btnManageVendors = findViewById(R.id.btnManageVendors);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);

        // Initial load of stats
        updateDashboardStats();

        // Navigation Listeners
        btnManageEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageEventsActivity.class));
            }
        });

        btnManageVendors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, ManageVendorsActivity.class));
            }
        });

        btnViewBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Note: Ensure your Activity name matches "ViewBookingsActivity"
                startActivity(new Intent(AdminDashboardActivity.this, ViewBookingsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboardActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * Fetches the latest counts from the database and updates the UI cards.
     */
    private void updateDashboardStats() {
        if (dbHelper != null) {
            long eventCount = dbHelper.getEventCount();
            long bookingCount = dbHelper.getBookingCount();

            tvTotalEvents.setText(String.valueOf(eventCount));
            tvTotalBookings.setText(String.valueOf(bookingCount));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh counts whenever the admin returns to the dashboard
        updateDashboardStats();
    }
}