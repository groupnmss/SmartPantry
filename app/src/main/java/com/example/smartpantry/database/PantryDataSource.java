package com.example.smartpantry.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * CRUD data source for pantry items.
 */
public class PantryDataSource {

    private static final String TAG = "PantryDataSource";

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public PantryDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null && database.isOpen()) dbHelper.close();
    }

    /** Insert a new pantry item. Returns its new id, or -1 on error. */
    public long insertPantryItem(PantryItem item) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.PANTRY_NAME, item.getName());
            cv.put(DatabaseHelper.PANTRY_QUANTITY, item.getQuantity());
            cv.put(DatabaseHelper.PANTRY_UNIT, item.getUnit());
            cv.put(DatabaseHelper.PANTRY_EXPIRY, item.getExpiryDate());
            long id = database.insert(DatabaseHelper.TABLE_PANTRY, null, cv);
            Log.i(TAG, "insertPantryItem: id=" + id);
            return id;
        } catch (Exception e) {
            Log.e(TAG, "insertPantryItem failed", e);
            return -1;
        }
    }

    /** Update an existing pantry item. Returns true on success. */
    public boolean updatePantryItem(PantryItem item) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.PANTRY_NAME, item.getName());
            cv.put(DatabaseHelper.PANTRY_QUANTITY, item.getQuantity());
            cv.put(DatabaseHelper.PANTRY_UNIT, item.getUnit());
            cv.put(DatabaseHelper.PANTRY_EXPIRY, item.getExpiryDate());
            int rows = database.update(
                    DatabaseHelper.TABLE_PANTRY,
                    cv,
                    DatabaseHelper.PANTRY_ID + " = ?",
                    new String[]{String.valueOf(item.getId())}
            );
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "updatePantryItem failed", e);
            return false;
        }
    }

    /** Delete by id. Returns true on success. */
    public boolean deletePantryItem(long id) {
        try {
            int rows = database.delete(
                    DatabaseHelper.TABLE_PANTRY,
                    DatabaseHelper.PANTRY_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "deletePantryItem failed", e);
            return false;
        }
    }

    /** Fetch all pantry items, sorted by name. */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_PANTRY,
                    null, null, null, null, null,
                    DatabaseHelper.PANTRY_NAME + " ASC"
            );
            while (cursor.moveToNext()) {
                items.add(cursorToPantryItem(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllPantryItems failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return items;
    }

    /** Fetch a single pantry item by id, or null if not found. */
    public PantryItem getPantryItem(long id) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_PANTRY,
                    null,
                    DatabaseHelper.PANTRY_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );
            if (cursor.moveToFirst()) return cursorToPantryItem(cursor);
        } catch (Exception e) {
            Log.e(TAG, "getPantryItem failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private PantryItem cursorToPantryItem(Cursor cursor) {
        PantryItem item = new PantryItem();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.PANTRY_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PANTRY_NAME)));
        item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PANTRY_QUANTITY)));
        item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PANTRY_UNIT)));
        item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PANTRY_EXPIRY)));
        return item;
    }
}
