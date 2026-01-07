package com.example.eventmanagementapp;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    private Spinner spinnerEvents;
    private ListView listVendors;
    private Button btnAddVendor;
    private DatabaseHelper dbHelper;
    private ArrayList<String> eventList;
    private ArrayList<Integer> eventIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_vendors);

        dbHelper = new DatabaseHelper(this);

        spinnerEvents = findViewById(R.id.spinnerEvents);
        listVendors = findViewById(R.id.listVendors);
        btnAddVendor = findViewById(R.id.btnAddVendor);

        loadEvents();
        loadVendors(0); // Load vendors for first event by default

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedEventId = eventIds.get(position);
                loadVendors(selectedEventId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAddVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = spinnerEvents.getSelectedItemPosition();
                if (selectedPosition >= 0) {
                    int selectedEventId = eventIds.get(selectedPosition);
                    Intent intent = new Intent(ManageVendorsActivity.this, AddVendorActivity.class);
                    intent.putExtra("EVENT_ID", selectedEventId);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadEvents() {
        eventList = new ArrayList<>();
        eventIds = new ArrayList<>();

        Cursor cursor = dbHelper.getAllEvents();
        if (cursor.moveToFirst()) {
            do {
                int eventId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_ID));
                String eventName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME));

                eventIds.add(eventId);
                eventList.add(eventName);
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                eventList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEvents.setAdapter(adapter);
    }

    private void loadVendors(int eventId) {
        Cursor cursor = dbHelper.getVendorsByEvent(eventId);

        String[] from = {
                DatabaseHelper.COLUMN_VENDOR_NAME,
                DatabaseHelper.COLUMN_VENDOR_SERVICE,
                DatabaseHelper.COLUMN_VENDOR_CONTACT
        };

        int[] to = {
                android.R.id.text1,
                android.R.id.text2
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                from,
                to,
                0
        );

        listVendors.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
        int selectedPosition = spinnerEvents.getSelectedItemPosition();
        if (selectedPosition >= 0) {
            int selectedEventId = eventIds.get(selectedPosition);
            loadVendors(selectedEventId);
        }
    }
}