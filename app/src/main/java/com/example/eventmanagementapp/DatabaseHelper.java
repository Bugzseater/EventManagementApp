package com.example.eventmanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log; // Add this import

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper"; // Add this line
    private static final String DATABASE_NAME = "EventManagement.db";
    private static final int DATABASE_VERSION = 1;

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

    // Add this method to check if database is working
    public boolean isDatabaseReady() {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            int tableCount = cursor.getCount();
            cursor.close();
            Log.d(TAG, "Database has " + tableCount + " tables");
            return tableCount >= 4; // Should have at least 4 tables
        } catch (Exception e) {
            Log.e(TAG, "Error checking database: " + e.getMessage(), e);
            return false;
        }
    }

    // REMOVE THE DUPLICATE getAllEvents() method - keep only this one:
    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Log.d(TAG, "Getting all events from database: " + db.getPath());

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
            Log.d(TAG, "Found " + cursor.getCount() + " events");

            // Debug: List all events
            if (cursor.moveToFirst()) {
                do {
                    String eventName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_NAME));
                    String eventDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EVENT_DATE));
                    Log.d(TAG, "Event: " + eventName + " on " + eventDate);
                } while (cursor.moveToNext());
                cursor.moveToFirst(); // Reset to first position
            }

            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error in getAllEvents: " + e.getMessage(), e);

            // Check if table exists
            Cursor tableCheck = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{TABLE_EVENTS}
            );
            boolean tableExists = tableCheck.getCount() > 0;
            tableCheck.close();
            Log.d(TAG, "Events table exists: " + tableExists);

            // Return empty cursor if table doesn't exist
            if (!tableExists) {
                Log.d(TAG, "Creating empty cursor");
                return db.rawQuery("SELECT null as " + COLUMN_EVENT_ID +
                        ", null as " + COLUMN_EVENT_NAME +
                        ", null as " + COLUMN_EVENT_DATE +
                        ", null as " + COLUMN_EVENT_LOCATION + " WHERE 0", null);
            }

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

    // Get event by ID
    public Cursor getEventById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_EVENT_ID, COLUMN_EVENT_NAME, COLUMN_EVENT_DESCRIPTION,
                COLUMN_EVENT_DATE, COLUMN_EVENT_TIME, COLUMN_EVENT_LOCATION,
                COLUMN_EVENT_PRICE, COLUMN_EVENT_CAPACITY};
        String selection = COLUMN_EVENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(eventId)};

        Log.d(TAG, "Getting event by ID: " + eventId);
        return db.query(TABLE_EVENTS, columns, selection, selectionArgs, null, null, null);
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
        String[] columns = {COLUMN_VENDOR_ID, COLUMN_VENDOR_NAME,
                COLUMN_VENDOR_SERVICE, COLUMN_VENDOR_CONTACT};
        String selection = COLUMN_EVENT_ID_FK + " = ?";
        String[] selectionArgs = {String.valueOf(eventId)};

        Log.d(TAG, "Getting vendors for event ID: " + eventId);
        return db.query(TABLE_VENDORS, columns, selection, selectionArgs, null, null, null);
    }

    // Create booking
    public boolean createBooking(int userId, int eventId, String bookingDate,
                                 int tickets, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_EVENT_ID_FK_BOOKING, eventId);
        values.put(COLUMN_BOOKING_DATE, bookingDate);
        values.put(COLUMN_NUMBER_OF_TICKETS, tickets);
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);

        long result = db.insert(TABLE_BOOKINGS, null, values);
        Log.d(TAG, "Create booking for user " + userId + ", event " + eventId + ": " + (result != -1));
        return result != -1;
    }

    // Get all bookings (for admin)
    public Cursor getAllBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, u." + COLUMN_USERNAME + ", e." + COLUMN_EVENT_NAME +
                " FROM " + TABLE_BOOKINGS + " b" +
                " JOIN " + TABLE_USERS + " u ON b." + COLUMN_USER_ID_FK + " = u." + COLUMN_ID +
                " JOIN " + TABLE_EVENTS + " e ON b." + COLUMN_EVENT_ID_FK_BOOKING + " = e." + COLUMN_EVENT_ID;
        Log.d(TAG, "Getting all bookings");
        return db.rawQuery(query, null);
    }

    // Get user bookings
    public Cursor getUserBookings(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT b.*, e." + COLUMN_EVENT_NAME + ", e." + COLUMN_EVENT_DATE +
                " FROM " + TABLE_BOOKINGS + " b" +
                " JOIN " + TABLE_EVENTS + " e ON b." + COLUMN_EVENT_ID_FK_BOOKING + " = e." + COLUMN_EVENT_ID +
                " WHERE b." + COLUMN_USER_ID_FK + " = " + userId;
        Log.d(TAG, "Getting bookings for user ID: " + userId);
        return db.rawQuery(query, null);
    }
}