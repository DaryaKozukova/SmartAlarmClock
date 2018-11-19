package com.example.acer.smartalarmclock;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "alarmTime";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "alarm";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_REPEAT= "year";
    public static final String COLUMN_STATUS= "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME +  "(" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_TIME
                + " TEXT, " + COLUMN_REPEAT + " TEXT," + COLUMN_STATUS + " TEXT);");
//        // добавление начальных данных
//        db.execSQL("INSERT INTO "+ TABLE_NAME +" (" + COLUMN_TIME
//                + ", " + COLUMN_REPEAT  + ") VALUES (1000, '123');");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
