package com.example.eventmanagementapp;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class ViewEventsActivity extends AppCompatActivity {
    private ListView listEvents;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        dbHelper = new DatabaseHelper(this);
        listEvents = findViewById(R.id.listEvents);
        userId = getIntent().getIntExtra("USER_ID", -1);

        loadEvents();

        listEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int eventId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_ID));

                Intent intent = new Intent(ViewEventsActivity.this, EventDetailActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("IS_ADMIN", false);
                startActivity(intent);
            }
        });
    }

    private void loadEvents() {
        Cursor cursor = dbHelper.getAllEvents();
        String[] from = {DatabaseHelper.COLUMN_EVENT_NAME, DatabaseHelper.COLUMN_EVENT_DATE,
                DatabaseHelper.COLUMN_EVENT_LOCATION};
        int[] to = {android.R.id.text1, android.R.id.text2};

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor,
                from,
                to,
                0);
        listEvents.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
}