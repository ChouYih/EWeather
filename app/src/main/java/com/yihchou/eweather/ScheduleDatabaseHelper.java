package com.yihchou.eweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "schedule";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_SCHEDULE1 = "schedule1";
    public static final String COLUMN_SCHEDULE2 = "schedule2";
    public static final String COLUMN_SCHEDULE3 = "schedule3";
    public static final String COLUMN_SCHEDULE4 = "schedule4";
    public static final String COLUMN_SCHEDULE5 = "schedule5";
    public static final String COLUMN_SCHEDULE6 = "schedule6";

    public ScheduleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_DATE + " TEXT PRIMARY KEY,"
                + COLUMN_SCHEDULE1 + " TEXT,"
                + COLUMN_SCHEDULE2 + " TEXT,"
                + COLUMN_SCHEDULE3 + " TEXT,"
                + COLUMN_SCHEDULE4 + " TEXT,"
                + COLUMN_SCHEDULE5 + " TEXT,"
                + COLUMN_SCHEDULE6 + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertSchedule(String date, String schedule1, String schedule2, String schedule3, String schedule4, String schedule5, String schedule6) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_SCHEDULE1, schedule1);
        values.put(COLUMN_SCHEDULE2, schedule2);
        values.put(COLUMN_SCHEDULE3, schedule3);
        values.put(COLUMN_SCHEDULE4, schedule4);
        values.put(COLUMN_SCHEDULE5, schedule5);
        values.put(COLUMN_SCHEDULE6, schedule6);
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Cursor getSchedule(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_DATE + "=?", new String[]{date}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean scheduleExists(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_DATE}, COLUMN_DATE + "=?", new String[]{date}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void updateSchedule(String date, String schedule1, String schedule2, String schedule3, String schedule4, String schedule5, String schedule6) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULE1, schedule1);
        values.put(COLUMN_SCHEDULE2, schedule2);
        values.put(COLUMN_SCHEDULE3, schedule3);
        values.put(COLUMN_SCHEDULE4, schedule4);
        values.put(COLUMN_SCHEDULE5, schedule5);
        values.put(COLUMN_SCHEDULE6, schedule6);
        db.update(TABLE_NAME, values, COLUMN_DATE + "=?", new String[]{date});
        db.close();
    }

    public void deleteSchedule(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_DATE + "=?", new String[]{date});
        db.close();
    }
}
