package com.example.eventmanagementapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {
    private EditText etEventName, etEventDescription, etEventDate, etEventTime,
            etEventLocation, etEventPrice, etEventCapacity;
    private Button btnSaveEvent;
    private DatabaseHelper dbHelper;
    private boolean isEditMode = false;
    private int eventId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etEventDate = findViewById(R.id.etEventDate);
        etEventTime = findViewById(R.id.etEventTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventPrice = findViewById(R.id.etEventPrice);
        etEventCapacity = findViewById(R.id.etEventCapacity);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        // Check if editing existing event
        if (getIntent().hasExtra("EVENT_ID")) {
            isEditMode = true;
            eventId = getIntent().getIntExtra("EVENT_ID", -1);
            btnSaveEvent.setText("Update Event");
            loadEventData();
        }

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void loadEventData() {
        if (eventId == -1) return;

        Cursor cursor = dbHelper.getEventById(eventId);
        if (cursor.moveToFirst()) {
            etEventName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_NAME)));
            etEventDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_DESCRIPTION)));
            etEventDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_DATE)));
            etEventTime.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_TIME)));
            etEventLocation.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_LOCATION)));
            etEventPrice.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_PRICE))));
            etEventCapacity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_CAPACITY))));
        }
        cursor.close();
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();
        String time = etEventTime.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String priceStr = etEventPrice.getText().toString().trim();
        String capacityStr = etEventCapacity.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || date.isEmpty() ||
                time.isEmpty() || location.isEmpty() || priceStr.isEmpty() ||
                capacityStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int capacity = Integer.parseInt(capacityStr);

        boolean success;
        if (isEditMode) {
            success = dbHelper.updateEvent(eventId, name, description, date, time, location, price, capacity);
            Toast.makeText(this, success ? "Event updated" : "Update failed", Toast.LENGTH_SHORT).show();
        } else {
            // Assuming admin ID is 1 (default admin)
            success = dbHelper.addEvent(name, description, date, time, location, price, capacity, 1);
            Toast.makeText(this, success ? "Event added" : "Add failed", Toast.LENGTH_SHORT).show();
        }

        if (success) {
            finish();
        }
    }
}