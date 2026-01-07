package com.example.eventmanagementapp;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewBookingsActivity extends AppCompatActivity {
    private ListView listBookings;
    private TextView tvNoBookings;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        dbHelper = new DatabaseHelper(this);

        listBookings = findViewById(R.id.listBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);

        loadBookings();
    }

    private void loadBookings() {
        Cursor cursor = dbHelper.getAllBookings();

        if (cursor.getCount() == 0) {
            tvNoBookings.setVisibility(View.VISIBLE);
            listBookings.setVisibility(View.GONE);
        } else {
            tvNoBookings.setVisibility(View.GONE);
            listBookings.setVisibility(View.VISIBLE);

            String[] from = {
                    DatabaseHelper.COLUMN_USERNAME,
                    DatabaseHelper.COLUMN_EVENT_NAME,
                    DatabaseHelper.COLUMN_BOOKING_DATE,
                    DatabaseHelper.COLUMN_NUMBER_OF_TICKETS,
                    DatabaseHelper.COLUMN_TOTAL_AMOUNT
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

            listBookings.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}