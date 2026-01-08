package com.example.eventmanagementapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity {
    private TextView tvEventName, tvEventDescription, tvEventDateTime,
            tvEventLocation, tvEventPrice, tvEventCapacity;
    private LinearLayout bookingSection, adminSection;
    private EditText etTickets, holderCardName, debitCardNumber;

    private Button btnBook, btnEdit, btnDelete;
    private DatabaseHelper dbHelper;
    private int eventId, userId;
    private boolean isAdmin;
    private double eventPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        dbHelper = new DatabaseHelper(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        userId = getIntent().getIntExtra("USER_ID", -1);
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Initialize views
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDescription = findViewById(R.id.tvEventDescription);
        tvEventDateTime = findViewById(R.id.tvEventDateTime);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventPrice = findViewById(R.id.tvEventPrice);
        tvEventCapacity = findViewById(R.id.tvEventCapacity);

        bookingSection = findViewById(R.id.bookingSection);
        adminSection = findViewById(R.id.adminSection);
        etTickets = findViewById(R.id.etTickets);
        btnBook = findViewById(R.id.btnBook);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        holderCardName = findViewById(R.id.holderCardName);
        debitCardNumber = findViewById(R.id.debitCardNumber);

        loadEventDetails();

        if (isAdmin) {
            adminSection.setVisibility(View.VISIBLE);
            setupAdminButtons();
        } else {
            bookingSection.setVisibility(View.VISIBLE);
            setupBookingButton();
        }
    }

    private void loadEventDetails() {
        Cursor cursor = dbHelper.getEventById(eventId);
        if (cursor.moveToFirst()) {
            tvEventName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_NAME)));
            tvEventDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_DESCRIPTION)));

            String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_DATE)) + " " +
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_TIME));
            tvEventDateTime.setText(dateTime);

            tvEventLocation.setText("Location: " + cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_LOCATION)));

            eventPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_PRICE));
            tvEventPrice.setText("Price: Rs. " + String.format("%,.2f", eventPrice));

            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EVENT_CAPACITY));
            tvEventCapacity.setText("Available Tickets: " + capacity);
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        cursor.close();
    }

    private void setupBookingButton() {
        btnBook.setOnClickListener(v -> {

            String ticketsStr = etTickets.getText().toString().trim();
            String cardName = holderCardName.getText().toString().trim();
            String cardNumber = debitCardNumber.getText().toString().trim();

            // 1️⃣ Validate tickets
            if (ticketsStr.isEmpty()) {
                etTickets.setError("Enter ticket count");
                return;
            }

            int tickets;
            try {
                tickets = Integer.parseInt(ticketsStr);
            } catch (NumberFormatException e) {
                etTickets.setError("Invalid number");
                return;
            }

            if (tickets <= 0) {
                etTickets.setError("At least 1 ticket required");
                return;
            }

            // 2️⃣ Validate card
            if (cardName.isEmpty()) {
                holderCardName.setError("Card holder name required");
                return;
            }

            if (cardNumber.isEmpty() || cardNumber.length() < 12) {
                debitCardNumber.setError("Invalid card number");
                return;
            }

            // 3️⃣ Calculate total
            double totalAmount = tickets * eventPrice;
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            // 4️⃣ Save booking
            boolean success = dbHelper.createBooking(
                    userId,
                    eventId,
                    currentDate,
                    tickets,
                    totalAmount,
                    cardName,
                    cardNumber
            );

            if (success) {
                Toast.makeText(this,
                        "Booking Successful!\nTotal: Rs. " + String.format("%,.2f", totalAmount),
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Booking Failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupAdminButtons() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailActivity.this, AddEventActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EventDetailActivity.this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHelper.deleteEvent(eventId)) {
                                    Toast.makeText(EventDetailActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(EventDetailActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
}