package com.example.eventmanagementapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManageEventsActivity extends AppCompatActivity {
    private static final String TAG = "ManageEventsActivity";
    private ListView listEvents;
    private Button btnAddEvent;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        Log.d(TAG, "ManageEventsActivity started");

        try {
            // Initialize DatabaseHelper
            dbHelper = new DatabaseHelper(this);

            // Check if database is ready
            if (!dbHelper.isDatabaseReady()) {
                Toast.makeText(this, "Database not ready. Please restart app.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Log.d(TAG, "DatabaseHelper initialized successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: " + e.getMessage(), e);
            Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        listEvents = findViewById(R.id.listEvents);
        btnAddEvent = findViewById(R.id.btnAddEvent);

        if (listEvents == null) {
            Log.e(TAG, "ListView not found in layout");
            Toast.makeText(this, "UI Error: ListView not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (btnAddEvent == null) {
            Log.e(TAG, "Button not found in layout");
            Toast.makeText(this, "UI Error: Button not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add Event button clicked");
                startActivity(new Intent(ManageEventsActivity.this, AddEventActivity.class));
            }
        });

        loadEvents();

        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    int eventId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_ID));
                    Log.d(TAG, "Event clicked, ID: " + eventId);

                    Intent intent = new Intent(ManageEventsActivity.this, EventDetailActivity.class);
                    intent.putExtra("EVENT_ID", eventId);
                    intent.putExtra("IS_ADMIN", true);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error on item click: " + e.getMessage(), e);
                    Toast.makeText(ManageEventsActivity.this, "Error selecting event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d(TAG, "Activity setup completed");
    }

    private void loadEvents() {
        Log.d(TAG, "Loading events...");
        try {
            Cursor cursor = dbHelper.getAllEvents();
            Log.d(TAG, "Cursor received with " + cursor.getCount() + " items");

            String[] from = {
                    DatabaseHelper.COLUMN_EVENT_NAME,
                    DatabaseHelper.COLUMN_EVENT_DATE,
                    DatabaseHelper.COLUMN_EVENT_LOCATION
            };
            int[] to = {android.R.id.text1, android.R.id.text2};

            adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    from,
                    to,
                    0
            );

            listEvents.setAdapter(adapter);
            Log.d(TAG, "Adapter set on ListView");

        } catch (Exception e) {
            Log.e(TAG, "Error loading events: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume - refreshing events list");
        if (dbHelper != null) {
            loadEvents();
        }
    }
}