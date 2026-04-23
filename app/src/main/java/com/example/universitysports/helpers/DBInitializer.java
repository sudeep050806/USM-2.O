package com.example.universitysports.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.universitysports.models.Ground;

import java.util.List;

/**
 * Database initialization utility
 * Used to seed initial data if database is empty
 */
public class DBInitializer {

    private DBHelper dbHelper;

    public DBInitializer(Context context) {
        dbHelper = new DBHelper(context);
    }

    /**
     * Initialize database with default data if needed
     */
    public void initialize() {
        // The DBHelper already inserts sample data in onCreate()
        // This method can be extended for more complex seeding
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Check if data exists and add more if needed
        db.close();
    }

    /**
     * Clear all data (for testing purposes only)
     */
    public void clearAllData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM bookings");
        db.execSQL("DELETE FROM events");
        db.execSQL("DELETE FROM grounds");
        db.execSQL("DELETE FROM users");
        db.close();
    }

    /**
     * Add a test ground
     */
    public long addTestGround() {
        Ground ground = new Ground();
        ground.setName("Test Ground");
        ground.setDescription("A test ground");
        ground.setSportType("General");
        ground.setCapacity(50);
        ground.setOpeningTime("06:00");
        ground.setClosingTime("22:00");
        return dbHelper.addGround(ground);
    }

    /**
     * Get ground count
     */
    public int getGroundCount() {
        List<Ground> grounds = dbHelper.getAllGrounds();
        return grounds.size();
    }
}