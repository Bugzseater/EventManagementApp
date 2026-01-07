package com.example.eventmanagementapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {
    private Button btnManageEvents, btnManageVendors, btnViewBookings, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnManageEvents = findViewById(R.id.btnManageEvents);
        btnManageVendors = findViewById(R.id.btnManageVendors);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);

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
}