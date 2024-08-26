package com.sp.userfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userfitness.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "workout_data";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_ACTIVITY_NAME = "activity_name";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_CALORIES_BURNT = "calories_burnt";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_ACTIVITY_NAME + " TEXT, " +
                COLUMN_DURATION + " INTEGER, " +
                COLUMN_CALORIES_BURNT + " REAL)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertWorkoutData(String date, String activityName, long duration, double caloriesBurnt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_ACTIVITY_NAME, activityName);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_CALORIES_BURNT, caloriesBurnt);
        return db.insert(TABLE_NAME, null, values);
    }

    public Cursor getAllWorkouts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_DATE + " DESC";
        return db.rawQuery(query, null);
    }

    public long getLatestWorkoutDuration() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long duration = -1; // Default value indicating no data found

        try {
            // Query to get the latest entry based on COLUMN_ID
            String query = "SELECT " + COLUMN_DURATION + " FROM " + TABLE_NAME +
                    " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_DURATION);
                if (columnIndex != -1) {
                    duration = cursor.getLong(columnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed to avoid memory leaks
            }
        }

        return duration;
    }

    public double getLatestCaloriesBurnt() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double caloriesBurnt = -1; // Default value indicating no data found

        try {
            // Query to get the latest entry based on COLUMN_ID
            String query = "SELECT " + COLUMN_CALORIES_BURNT + " FROM " + TABLE_NAME +
                    " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_CALORIES_BURNT);
                if (columnIndex != -1) {
                    caloriesBurnt = cursor.getDouble(columnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed to avoid memory leaks
            }
        }

        return caloriesBurnt;
    }

    public double getWeeklyTotalCalories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        double totalCalories = 0;

        try {
            // Query to get the sum of calories burned for the current week
            String query = "SELECT SUM(" + COLUMN_CALORIES_BURNT + ") FROM " + TABLE_NAME +
                    " WHERE strftime('%W', " + COLUMN_DATE + ") = strftime('%W', 'now')";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("SUM(" + COLUMN_CALORIES_BURNT + ")");
                if (columnIndex != -1) {
                    totalCalories = cursor.getDouble(columnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed to avoid memory leaks
            }
        }

        return totalCalories;
    }

    public long getWeeklyTotalDuration() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long totalDuration = 0;

        try {
            // Query to get the sum of durations for the current week
            String query = "SELECT SUM(" + COLUMN_DURATION + ") FROM " + TABLE_NAME +
                    " WHERE strftime('%W', " + COLUMN_DATE + ") = strftime('%W', 'now')";
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("SUM(" + COLUMN_DURATION + ")");
                if (columnIndex != -1) {
                    totalDuration = cursor.getLong(columnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Ensure the cursor is closed to avoid memory leaks
            }
        }

        return totalDuration;
    }

    public Map<String, Double> getDailyCalories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String, Double> dailyCalories = new LinkedHashMap<>();

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String day : daysOfWeek) {
            double calories = 0;
            Cursor cursor = db.rawQuery(
                    "SELECT SUM(" + COLUMN_CALORIES_BURNT + ") FROM " + TABLE_NAME +
                            " WHERE strftime('%W', " + COLUMN_DATE + ") = strftime('%W', 'now')" +
                            " AND strftime('%w', " + COLUMN_DATE + ") = ?", new String[]{String.valueOf(dayOfWeek(day))}
            );
            if (cursor != null && cursor.moveToFirst()) {
                calories = cursor.getDouble(0);
                cursor.close();
            }
            dailyCalories.put(day, calories);
        }
        return dailyCalories;
    }

    private int dayOfWeek(String day) {
        switch (day) {
            case "Mon": return 1;
            case "Tue": return 2;
            case "Wed": return 3;
            case "Thu": return 4;
            case "Fri": return 5;
            case "Sat": return 6;
            case "Sun": return 0;
            default: return -1;
        }
    }

}