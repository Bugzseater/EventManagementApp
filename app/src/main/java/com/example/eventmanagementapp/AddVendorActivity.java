// File: AddVendorActivity.java
package com.example.eventmanagementapp;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddVendorActivity extends AppCompatActivity {
    private static final String TAG = "AddVendorActivity";
    private TextView tvEventName;
    private EditText etVendorName, etVendorService, etVendorContact;
    private Button btnSaveVendor;
    private DatabaseHelper dbHelper;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Log.d(TAG, "AddVendorActivity started");

        dbHelper = new DatabaseHelper(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        Log.d(TAG, "Received event ID: " + eventId);

        tvEventName = findViewById(R.id.tvEventName);
        etVendorName = findViewById(R.id.etVendorName);
        etVendorService = findViewById(R.id.etVendorService);
        etVendorContact = findViewById(R.id.etVendorContact);
        btnSaveVendor = findViewById(R.id.btnSaveVendor);

        if (getIntent() == null || !getIntent().hasExtra("EVENT_ID")) {
            Toast.makeText(this, "Event not selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        if (eventId <= 0) {
            Toast.makeText(this, "Invalid event ID!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        loadEventName();

        btnSaveVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVendor();
            }
        });
    }

    private void loadEventName() {
        try {
            String eventName = dbHelper.getEventNameById(eventId);
            if (eventName != null) {
                tvEventName.setText("Event: " + eventName);
                Log.d(TAG, "Loaded event: " + eventName);
            } else {
                tvEventName.setText("Event: Unknown (ID: " + eventId + ")");
                Log.d(TAG, "Event not found for ID: " + eventId);
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading event name: " + e.getMessage(), e);
            tvEventName.setText("Event: Error loading");
            Toast.makeText(this, "Error loading event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVendor() {
        String name = etVendorName.getText().toString().trim();
        String service = etVendorService.getText().toString().trim();
        String contact = etVendorContact.getText().toString().trim();

        if (name.isEmpty()) {
            etVendorName.setError("Vendor name is required");
            return;
        }

        if (service.isEmpty()) {
            etVendorService.setError("Service is required");
            return;
        }

        if (contact.isEmpty()) {
            etVendorContact.setError("Contact is required");
            return;
        }

        Log.d(TAG, "Saving vendor - Name: " + name + ", Service: " + service + ", Contact: " + contact);

        boolean success = dbHelper.addVendor(name, service, contact, eventId);
        if (success) {
            Log.d(TAG, "Vendor added successfully");
            Toast.makeText(this, "Vendor added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Log.e(TAG, "Failed to add vendor");
            Toast.makeText(this, "Failed to add vendor", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AddVendorActivity destroyed");
    }
}