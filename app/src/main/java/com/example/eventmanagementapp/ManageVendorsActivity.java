package com.example.eventmanagementapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageVendorsActivity extends AppCompatActivity {

    private static final String TAG = "ManageVendorsActivity";

    private Spinner spinnerEvents;
    private ListView listVendors;
    private Button btnAddVendor;

    private DatabaseHelper dbHelper;

    private ArrayList<String> eventList = new ArrayList<>();
    private ArrayList<Integer> eventIds = new ArrayList<>();

    private SimpleCursorAdapter vendorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vendors);

        Log.d(TAG, "Activity started");

        dbHelper = new DatabaseHelper(this);

        spinnerEvents = findViewById(R.id.spinnerEvents);
        listVendors = findViewById(R.id.listVendors);
        btnAddVendor = findViewById(R.id.btnAddVendor);

        setupVendorList();
        loadEvents();

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position < 0 || position >= eventIds.size()) return;

                int eventId = eventIds.get(position);

                if (eventId <= 0) {
                    clearVendors();
                    return;
                }

                loadVendors(eventId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddVendor.setOnClickListener(v -> openAddVendorScreen());
    }

    // ------------------------ SETUP ------------------------

    private void setupVendorList() {
        vendorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[]{
                        DatabaseHelper.COLUMN_VENDOR_NAME,
                        DatabaseHelper.COLUMN_VENDOR_SERVICE
                },
                new int[]{
                        android.R.id.text1,
                        android.R.id.text2
                },
                0
        );

        listVendors.setAdapter(vendorAdapter);
    }

    // ------------------------ EVENTS ------------------------

    private void loadEvents() {

        eventList.clear();
        eventIds.clear();

        Cursor cursor = null;

        try {
            cursor = dbHelper.getAllEvents();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_NAME));

                    eventIds.add(id);
                    eventList.add(name);
                } while (cursor.moveToNext());
            } else {
                eventList.add("No events available");
                eventIds.add(-1);
            }

        } catch (Exception e) {
            Log.e(TAG, "loadEvents error", e);
            eventList.add("Error loading events");
            eventIds.add(-1);
        } finally {
            if (cursor != null) cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                eventList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(adapter);

        // Load vendors for first valid event
        if (!eventIds.isEmpty() && eventIds.get(0) > 0) {
            loadVendors(eventIds.get(0));
        } else {
            clearVendors();
        }
    }

    // ------------------------ VENDORS ------------------------

    private void loadVendors(int eventId) {

        if (eventId <= 0) {
            clearVendors();
            return;
        }

        Cursor cursor = dbHelper.getVendorsByEvent(eventId);

        Cursor old = vendorAdapter.swapCursor(cursor);
        if (old != null) old.close();
    }

    private void clearVendors() {
        Cursor old = vendorAdapter.swapCursor(null);
        if (old != null) old.close();
    }

    // ------------------------ ADD VENDOR ------------------------

    private void openAddVendorScreen() {

        int pos = spinnerEvents.getSelectedItemPosition();

        if (pos < 0 || pos >= eventIds.size()) {
            Toast.makeText(this, "Select an event first", Toast.LENGTH_SHORT).show();
            return;
        }

        int eventId = eventIds.get(pos);

        if (eventId <= 0) {
            Toast.makeText(this, "Invalid event selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AddVendorActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);
    }

    // ------------------------ LIFECYCLE ------------------------

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearVendors();
    }
}
