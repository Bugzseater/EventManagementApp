package com.example.eventmanagementapp;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddVendorActivity extends AppCompatActivity {
    private TextView tvEventName;
    private EditText etVendorName, etVendorService, etVendorContact;
    private Button btnSaveVendor;
    private DatabaseHelper dbHelper;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);

        dbHelper = new DatabaseHelper(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        tvEventName = findViewById(R.id.tvEventName);
        etVendorName = findViewById(R.id.etVendorName);
        etVendorService = findViewById(R.id.etVendorService);
        etVendorContact = findViewById(R.id.etVendorContact);
        btnSaveVendor = findViewById(R.id.btnSaveVendor);

        loadEventName();

        btnSaveVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVendor();
            }
        });
    }

    private void loadEventName() {
        if (eventId == -1) return;

        Cursor cursor = dbHelper.getEventById(eventId);
        if (cursor.moveToFirst()) {
            String eventName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME));
            tvEventName.setText("Event: " + eventName);
        }
        cursor.close();
    }

    private void saveVendor() {
        String name = etVendorName.getText().toString().trim();
        String service = etVendorService.getText().toString().trim();
        String contact = etVendorContact.getText().toString().trim();

        if (name.isEmpty() || service.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.addVendor(name, service, contact, eventId)) {
            Toast.makeText(this, "Vendor added successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add vendor", Toast.LENGTH_SHORT).show();
        }
    }
}