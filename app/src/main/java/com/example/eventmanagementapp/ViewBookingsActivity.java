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
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookings);

        dbHelper = new DatabaseHelper(this);

        listBookings = findViewById(R.id.listBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);


        loadBookings();

        listBookings.setOnItemLongClickListener((parent, view, position, id) -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Delete Booking")
                    .setMessage("Do you want to delete this booking?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        boolean success = dbHelper.deleteBooking((int) id);

                        if (success) {
                            loadBookings();
                            android.widget.Toast.makeText(this, "Booking deleted", android.widget.Toast.LENGTH_SHORT).show();
                        } else {
                            android.widget.Toast.makeText(this, "Delete failed", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });

    }

    private void loadBookings() {
        Cursor cursor = dbHelper.getAllBookings();

        if (cursor == null || cursor.getCount() == 0) {
            tvNoBookings.setVisibility(View.VISIBLE);
            listBookings.setVisibility(View.GONE);
            return;
        }

        tvNoBookings.setVisibility(View.GONE);
        listBookings.setVisibility(View.VISIBLE);

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.row_booking,
                cursor,
                new String[]{
                        DatabaseHelper.COLUMN_EVENT_NAME,
                        DatabaseHelper.COLUMN_BOOKING_DATE,
                        DatabaseHelper.COLUMN_NUMBER_OF_TICKETS,
                        DatabaseHelper.COLUMN_TOTAL_AMOUNT
                },
                new int[]{
                        R.id.tvTitle,
                        R.id.tvDate,
                        R.id.tvTickets,
                        R.id.tvPrice
                },
                0
        );

        adapter.setViewBinder((view, c, columnIndex) -> {
            String col = c.getColumnName(columnIndex);

            if (col.equals(DatabaseHelper.COLUMN_BOOKING_DATE)) {
                ((TextView) view).setText("Booking Date: " + c.getString(columnIndex));
                return true;
            }

            if (col.equals(DatabaseHelper.COLUMN_NUMBER_OF_TICKETS)) {
                ((TextView) view).setText("Tickets: " + c.getInt(columnIndex));
                return true;
            }

            if (col.equals(DatabaseHelper.COLUMN_TOTAL_AMOUNT)) {
                double amount = c.getDouble(columnIndex);
                ((TextView) view).setText("Total: Rs. " + String.format("%,.2f", amount));
                return true;
            }

            return false;
        });

        listBookings.setAdapter(adapter);
    }




    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}