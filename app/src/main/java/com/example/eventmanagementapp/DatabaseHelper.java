package com.example.eventmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "EventManagement.db";
    private static final int DATABASE_VERSION = 3;

    // User table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_MOBILE = "mobile";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ROLE = "role";

    // Events table
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_EVENT_ID = "event_id";
    public static final String COLUMN_EVENT_NAME = "event_name";
    public static final String COLUMN_EVENT_DESCRIPTION = "event_description";
    public static final String COLUMN_EVENT_DATE = "event_date";
    public static final String COLUMN_EVENT_TIME = "event_time";
    public static final String COLUMN_EVENT_LOCATION = "event_location";
    public static final String COLUMN_EVENT_PRICE = "event_price";
    public static final String COLUMN_EVENT_CAPACITY = "event_capacity";
    public static final String COLUMN_CREATED_BY = "created_by";

    // Vendors table
    public static final String TABLE_VENDORS = "vendors";
    public static final String COLUMN_VENDOR_ID = "vendor_id";
    public static final String COLUMN_VENDOR_NAME = "vendor_name";
    public static final String COLUMN_VENDOR_SERVICE = "vendor_service";
    public static final String COLUMN_VENDOR_CONTACT = "vendor_contact";
    public static final String COLUMN_EVENT_ID_FK = "event_id_fk";

    // Bookings table
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKING_ID = "booking_id";
    public static final String COLUMN_USER_ID_FK = "user_id_fk";
    public static final String COLUMN_EVENT_ID_FK_BOOKING = "event_id_fk";
    public static final String COLUMN_BOOKING_DATE = "booking_date";
    public static final String COLUMN_NUMBER_OF_TICKETS = "number_of_tickets";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    public static final String COLUMN_BOOKING_STATUS = "booking_status";

    public static final String COLUMN_CARD_NAME = "card_name";
    public static final String COLUMN_CARD_NUMBER = "card_number";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");

        try {
            // Create users table
            String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USERNAME + " TEXT,"
                    + COLUMN_EMAIL + " TEXT UNIQUE,"
                    + COLUMN_PASSWORD + " TEXT,"
                    + COLUMN_MOBILE + " TEXT,"
                    + COLUMN_ADDRESS + " TEXT,"
                    + COLUMN_ROLE + " TEXT DEFAULT 'user')";
            db.execSQL(CREATE_USERS_TABLE);
            Log.d(TAG, "Users table created");

            // Create events table
            String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                    + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EVENT_NAME + " TEXT,"
                    + COLUMN_EVENT_DESCRIPTION + " TEXT,"
                    + COLUMN_EVENT_DATE + " TEXT,"
                    + COLUMN_EVENT_TIME + " TEXT,"
                    + COLUMN_EVENT_LOCATION + " TEXT,"
                    + COLUMN_EVENT_PRICE + " REAL,"
                    + COLUMN_EVENT_CAPACITY + " INTEGER,"
                    + COLUMN_CREATED_BY + " INTEGER)";
            db.execSQL(CREATE_EVENTS_TABLE);
            Log.d(TAG, "Events table created");

            // Create vendors table
            String CREATE_VENDORS_TABLE = "CREATE TABLE " + TABLE_VENDORS + "("
                    + COLUMN_VENDOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_VENDOR_NAME + " TEXT,"
                    + COLUMN_VENDOR_SERVICE + " TEXT,"
                    + COLUMN_VENDOR_CONTACT + " TEXT,"
                    + COLUMN_EVENT_ID_FK + " INTEGER)";
            db.execSQL(CREATE_VENDORS_TABLE);
            Log.d(TAG, "Vendors table created");

            // Create bookings table
            String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                    + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_ID_FK + " INTEGER,"
                    + COLUMN_EVENT_ID_FK_BOOKING + " INTEGER,"
                    + COLUMN_BOOKING_DATE + " TEXT,"
                    + COLUMN_NUMBER_OF_TICKETS + " INTEGER,"
                    + COLUMN_TOTAL_AMOUNT + " REAL,"
                    + COLUMN_CARD_NAME + " TEXT,"
                    + COLUMN_CARD_NUMBER + " TEXT,"
                    + COLUMN_BOOKING_STATUS + " TEXT DEFAULT 'confirmed')";
            db.execSQL(CREATE_BOOKINGS_TABLE);
            Log.d(TAG, "Bookings table created");



            // Insert default admin user
            ContentValues adminValues = new ContentValues();
            adminValues.put(COLUMN_USERNAME, "admin");
            adminValues.put(COLUMN_EMAIL, "admin@event.com");
            adminValues.put(COLUMN_PASSWORD, "admin123");
            adminValues.put(COLUMN_ROLE, "admin");
            long adminId = db.insert(TABLE_USERS, null, adminValues);
            if (adminId != -1) {
                Log.d(TAG, "Default admin user created with ID: " + adminId);
            } else {
                Log.e(TAG, "Failed to create default admin user");
            }

            // Add demo events
            addDemoEvents(db);

        } catch (Exception e) {
            Log.e(TAG, "Error creating database: " + e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
            onCreate(db);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database: " + e.getMessage(), e);
        }
    }

    // Add demo events directly in onCreate
    private void addDemoEvents(SQLiteDatabase db) {
        Log.d(TAG, "Adding demo Sri Lankan events...");

        // Demo Sri Lankan Events
        addDemoEvent(db, "Galle Music Festival 2024",
                "Annual music festival featuring local and international artists at Galle Fort",
                "2024-03-15", "18:00", "Galle Fort, Galle", 5000.00, 500, 1);

        addDemoEvent(db, "Colombo Food Fest",
                "Experience the best of Sri Lankan cuisine from all provinces",
                "2024-03-20", "10:00", "Viharamahadevi Park, Colombo", 2000.00, 300, 1);

        addDemoEvent(db, "Kandy Perahera Exhibition",
                "Traditional cultural exhibition before the Esala Perahera",
                "2024-07-25", "14:00", "Kandy City Center", 1500.00, 400, 1);

        addDemoEvent(db, "Sri Lanka Tech Summit",
                "Technology and innovation conference with industry leaders",
                "2024-04-10", "09:00", "Cinnamon Grand, Colombo", 8000.00, 200, 1);

        addDemoEvent(db, "Negombo Beach Carnival",
                "Beach party with DJs, food stalls, and water sports",
                "2024-05-01", "16:00", "Negombo Beach", 3000.00, 600, 1);

        addDemoEvent(db, "Jaffna Cultural Festival",
                "Celebrate Tamil culture, food, and traditions",
                "2024-06-15", "09:00", "Jaffna Public Library", 1000.00, 800, 1);

        Log.d(TAG, "6 demo events added successfully");
    }

    private void addDemoEvent(SQLiteDatabase db, String name, String description,
                              String date, String time, String location,
                              double price, int capacity, int createdBy) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, name);
        values.put(COLUMN_EVENT_DESCRIPTION, description);
        values.put(COLUMN_EVENT_DATE, date);
        values.put(COLUMN_EVENT_TIME, time);
        values.put(COLUMN_EVENT_LOCATION, location);
        values.put(COLUMN_EVENT_PRICE, price);
        values.put(COLUMN_EVENT_CAPACITY, capacity);
        values.put(COLUMN_CREATED_BY, createdBy);

        long result = db.insert(TABLE_EVENTS, null, values);
        if (result != -1) {
            Log.d(TAG, "Demo event added: " + name);
        } else {
            Log.e(TAG, "Failed to add demo event: " + name);
        }
    }

    // Get all events - FIXED with _id
// Get all events - FIXED for SimpleCursorAdapter
    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "Getting all events from database");

        try {
            // SimpleCursorAdapter REQUIRES a column named "_id"
            String query = "SELECT " + COLUMN_EVENT_ID + " as _id, " +
                    COLUMN_EVENT_NAME + ", " +
                    COLUMN_EVENT_DATE + ", " +
                    COLUMN_EVENT_LOCATION + ", " +
                    COLUMN_EVENT_DESCRIPTION + ", " +
                    COLUMN_EVENT_TIME + ", " +
                    COLUMN_EVENT_PRICE + ", " +
                    COLUMN_EVENT_CAPACITY +
                    " FROM " + TABLE_EVENTS +
                    " ORDER BY " + COLUMN_EVENT_DATE + " ASC";

            Cursor cursor = db.rawQuery(query, null);
            Log.d(TAG, "Found " + cursor.getCount() + " events");
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error in getAllEvents: " + e.getMessage(), e);
            throw e;
        }
    }

    // User registration
    public boolean registerUser(String username, String email, String password, String mobile, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_ADDRESS, address);

        long result = db.insert(TABLE_USERS, null, values);
        Log.d(TAG, "User registration result: " + (result != -1));
        return result != -1;
    }

    // Check if user exists
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        Log.d(TAG, "User check for " + email + ": " + (count > 0));
        return count > 0;
    }

    // Get user role
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            cursor.close();
            Log.d(TAG, "User role for " + email + ": " + role);
            return role;
        }
        cursor.close();
        Log.d(TAG, "No role found for " + email);
        return null;
    }

    // Get user ID by email
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            cursor.close();
            Log.d(TAG, "User ID for " + email + ": " + id);
            return id;
        }
        cursor.close();
        Log.d(TAG, "No user found for " + email);
        return -1;
    }

    // Add event
    public boolean addEvent(String name, String description, String date, String time,
                            String location, double price, int capacity, int createdBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, name);
        values.put(COLUMN_EVENT_DESCRIPTION, description);
        values.put(COLUMN_EVENT_DATE, date);
        values.put(COLUMN_EVENT_TIME, time);
        values.put(COLUMN_EVENT_LOCATION, location);
        values.put(COLUMN_EVENT_PRICE, price);
        values.put(COLUMN_EVENT_CAPACITY, capacity);
        values.put(COLUMN_CREATED_BY, createdBy);

        long result = db.insert(TABLE_EVENTS, null, values);
        Log.d(TAG, "Add event '" + name + "': " + (result != -1));
        return result != -1;
    }

    // Get event name by ID - Add this to DatabaseHelper class
    public String getEventNameById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String eventName = null;
        Cursor cursor = null;

        try {
            String query = "SELECT " + COLUMN_EVENT_NAME +
                    " FROM " + TABLE_EVENTS +
                    " WHERE " + COLUMN_EVENT_ID + " = ?";

            cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});

            if (cursor != null && cursor.moveToFirst()) {
                eventName = cursor.getString(0);
                Log.d(TAG, "Found event name for ID " + eventId + ": " + eventName);
            } else {
                Log.d(TAG, "No event found with ID: " + eventId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting event name: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return eventName;
    }
    // Get event by ID - FIXED version
    public Cursor getEventById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "Getting event by ID: " + eventId);

        try {
            // Use rawQuery for simplicity
            String query = "SELECT * FROM " + TABLE_EVENTS +
                    " WHERE " + COLUMN_EVENT_ID + " = ?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(eventId)});

            // Debug: Check what columns we got
            if (cursor != null && cursor.moveToFirst()) {
                String[] columns = cursor.getColumnNames();
                Log.d(TAG, "Columns in event cursor:");
                for (String column : columns) {
                    Log.d(TAG, "  - " + column);
                }
            }

            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error in getEventById: " + e.getMessage(), e);
            return null;
        }
    }

    // Update event
    public boolean updateEvent(int eventId, String name, String description, String date,
                               String time, String location, double price, int capacity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_NAME, name);
        values.put(COLUMN_EVENT_DESCRIPTION, description);
        values.put(COLUMN_EVENT_DATE, date);
        values.put(COLUMN_EVENT_TIME, time);
        values.put(COLUMN_EVENT_LOCATION, location);
        values.put(COLUMN_EVENT_PRICE, price);
        values.put(COLUMN_EVENT_CAPACITY, capacity);

        String whereClause = COLUMN_EVENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(eventId)};

        int result = db.update(TABLE_EVENTS, values, whereClause, whereArgs);
        Log.d(TAG, "Update event ID " + eventId + ": " + (result > 0));
        return result > 0;
    }

    // Delete event
    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_EVENT_ID + " = ?";
        String[] whereArgs = {String.valueOf(eventId)};

        int result = db.delete(TABLE_EVENTS, whereClause, whereArgs);
        Log.d(TAG, "Delete event ID " + eventId + ": " + (result > 0));
        return result > 0;
    }

    // Add vendor
    public boolean addVendor(String name, String service, String contact, int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VENDOR_NAME, name);
        values.put(COLUMN_VENDOR_SERVICE, service);
        values.put(COLUMN_VENDOR_CONTACT, contact);
        values.put(COLUMN_EVENT_ID_FK, eventId);

        long result = db.insert(TABLE_VENDORS, null, values);
        Log.d(TAG, "Add vendor '" + name + "' for event " + eventId + ": " + (result != -1));
        return result != -1;
    }

    // Get vendors by event ID
    public Cursor getVendorsByEvent(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SimpleCursorAdapter REQUIRES a column named "_id"
        String query = "SELECT " + COLUMN_VENDOR_ID + " as _id, " +
                COLUMN_VENDOR_NAME + ", " +
                COLUMN_VENDOR_SERVICE + ", " +
                COLUMN_VENDOR_CONTACT +
                " FROM " + TABLE_VENDORS +
                " WHERE " + COLUMN_EVENT_ID_FK + " = ?" +
                " ORDER BY " + COLUMN_VENDOR_NAME + " ASC";

        Log.d(TAG, "Getting vendors for event ID: " + eventId);
        return db.rawQuery(query, new String[]{String.valueOf(eventId)});
    }

    // Create booking
    public boolean createBooking(int userId, int eventId, String bookingDate,
                                 int tickets, double totalAmount,
                                 String cardName, String cardNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_EVENT_ID_FK_BOOKING, eventId);
        values.put(COLUMN_BOOKING_DATE, bookingDate);
        values.put(COLUMN_NUMBER_OF_TICKETS, tickets);
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_CARD_NAME, cardName);
        values.put(COLUMN_CARD_NUMBER, cardNumber);
        values.put(COLUMN_BOOKING_STATUS, "confirmed");

        long result = db.insert(TABLE_BOOKINGS, null, values);

        if (result == -1) {
            Log.e(TAG, "Booking insert failed");
        } else {
            Log.d(TAG, "Booking successful. ID: " + result);
        }

        return result != -1;
    }


    // Get all bookings (for admin)
    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT b." + COLUMN_BOOKING_ID + " AS _id, " +
                        "u." + COLUMN_USERNAME + ", " +
                        "e." + COLUMN_EVENT_NAME + ", " +
                        "b." + COLUMN_BOOKING_DATE + ", " +
                        "b." + COLUMN_NUMBER_OF_TICKETS + ", " +
                        "b." + COLUMN_TOTAL_AMOUNT + ", " +
                        "b." + COLUMN_CARD_NAME + ", " +
                        "b." + COLUMN_CARD_NUMBER +
                        " FROM " + TABLE_BOOKINGS + " b " +
                        " JOIN " + TABLE_USERS + " u ON b." + COLUMN_USER_ID_FK + " = u." + COLUMN_ID +
                        " JOIN " + TABLE_EVENTS + " e ON b." + COLUMN_EVENT_ID_FK_BOOKING + " = e." + COLUMN_EVENT_ID;

        return db.rawQuery(query, null);
    }


    // Get user bookings
    public Cursor getUserBookings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT b." + COLUMN_BOOKING_ID + " AS _id, " +
                        "e." + COLUMN_EVENT_NAME + ", " +
                        "e." + COLUMN_EVENT_DATE + ", " +
                        "b." + COLUMN_BOOKING_DATE + ", " +
                        "b." + COLUMN_NUMBER_OF_TICKETS + ", " +
                        "b." + COLUMN_TOTAL_AMOUNT + ", " +
                        "b." + COLUMN_CARD_NAME + ", " +
                        "b." + COLUMN_CARD_NUMBER +
                        " FROM " + TABLE_BOOKINGS + " b " +
                        " JOIN " + TABLE_EVENTS + " e ON b." + COLUMN_EVENT_ID_FK_BOOKING + " = e." + COLUMN_EVENT_ID +
                        " WHERE b." + COLUMN_USER_ID_FK + " = ?";

        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public boolean deleteBooking(int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BOOKINGS,
                COLUMN_BOOKING_ID + " = ?",
                new String[]{String.valueOf(bookingId)});
        return result > 0;
    }


}