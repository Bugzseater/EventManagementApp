package com.example.eventmanagementapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ViewEventsActivity extends AppCompatActivity {
    private static final String TAG = "ViewEventsActivity";
    private ListView listEvents;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        Log.d(TAG, "ViewEventsActivity started");

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
        listEvents = findViewById(R.id.listEvents);

        // Get user ID from intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        Log.d(TAG, "User ID: " + userId);

        // Load events
        loadEvents();

        // Set item click listener
        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Cursor cursor = (Cursor) adapter.getItem(position);

                    // Get event ID from the "_id" column (which is actually event_id)
                    int eventId = cursor.getInt(cursor.getColumnIndex("_id"));

                    Log.d(TAG, "Event clicked - ID: " + eventId + ", Position: " + position);

                    // Start EventDetailActivity
                    Intent intent = new Intent(ViewEventsActivity.this, EventDetailActivity.class);
                    intent.putExtra("EVENT_ID", eventId);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);

                } catch (Exception e) {
                    Log.e(TAG, "Error clicking event: " + e.getMessage(), e);
                    Toast.makeText(ViewEventsActivity.this, "Error loading event", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadEvents() {
        try {
            Log.d(TAG, "Loading events...");

            Cursor cursor = dbHelper.getAllEvents();

            if (cursor == null) {
                Log.e(TAG, "Cursor is null!");
                Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cursor.getCount() == 0) {
                Log.d(TAG, "No events found");
                Toast.makeText(this, "No events available", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Loaded " + cursor.getCount() + " events");
            }

            // Check what columns we have
            String[] columnNames = cursor.getColumnNames();
            Log.d(TAG, "Available columns:");
            for (String name : columnNames) {
                Log.d(TAG, "  - " + name);
            }

            // Define which columns to show
            String[] from = {
                    DatabaseHelper.COLUMN_EVENT_NAME,
                    DatabaseHelper.COLUMN_EVENT_DATE,
                    DatabaseHelper.COLUMN_EVENT_LOCATION
            };

            // Define TextViews to populate
            int[] to = {
                    android.R.id.text1,  // Event name
                    android.R.id.text2   // Date and location
            };

            // Create adapter
            adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    from,
                    to,
                    0
            );

            // Set custom view binder to format the text
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (columnIndex == cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE)) {
                        TextView textView = (TextView) view;
                        String date = cursor.getString(columnIndex);
                        String location = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_LOCATION));
                        // Combine date and location
                        textView.setText(date + " | " + location);
                        return true;
                    }
                    return false;
                }
            });

            // Set adapter
            listEvents.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, "Error loading events: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list if needed
        refreshEvents();
    }

    private void refreshEvents() {
        if (adapter != null) {
            Cursor newCursor = dbHelper.getAllEvents();
            Cursor oldCursor = adapter.swapCursor(newCursor);
            if (oldCursor != null) {
                oldCursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up
        if (adapter != null) {
            Cursor cursor = adapter.getCursor();
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}