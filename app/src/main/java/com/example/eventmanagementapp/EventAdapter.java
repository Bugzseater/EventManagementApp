package com.example.eventmanagementapp;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class EventAdapter extends CursorAdapter {
    private DatabaseHelper dbHelper;

    public EventAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_event_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvEventName = view.findViewById(R.id.tvEventName);
        TextView tvEventDate = view.findViewById(R.id.tvEventDate);
        TextView tvEventLocation = view.findViewById(R.id.tvEventLocation);

        String eventName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_NAME));
        String eventDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_DATE));
        String eventLocation = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_EVENT_LOCATION));

        tvEventName.setText(eventName);
        tvEventDate.setText(eventDate);
        tvEventLocation.setText(eventLocation);
    }
}