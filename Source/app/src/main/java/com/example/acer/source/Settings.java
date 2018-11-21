package com.example.acer.source;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.AlarmClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import android.text.format.Time;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class Settings extends AppCompatActivity {
    private TimePicker mTimePicker;
    private ArrayList<Integer> mChooseDays;
    private String[] data;
    private boolean[] checkedDays;
    private int isSet = 0;
    private String mSetTime;
    private DatabaseHelper mHelper;
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;
    private long mId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mHelper = new DatabaseHelper(this);
        Time time = new Time();
        time.setToNow();

        mTimePicker = (TimePicker) findViewById(R.id.timePicker);

        mTimePicker.setCurrentHour(time.hour);
        mTimePicker.setCurrentMinute(time.minute);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if(minute < 10) mSetTime = new String(hourOfDay + ":0" + minute);
                else mSetTime = new String(hourOfDay + ":" + minute);
                if(hourOfDay < 10) {
                    mSetTime = "0" + mSetTime;
                    Log.i("TimeLog", Integer.toString(hourOfDay) +" time: "  + mSetTime);
                }
            }
        });
        mDatabase = mHelper.getWritableDatabase();

    }


    public void onButtonClick(View v){
        final ContentValues contentValues = new ContentValues();
        switch (v.getId()){
            case R.id.Save:
                if(mSetTime == null){
                    Time time = new Time();
                    time.setToNow();
                    if(time.minute < 10) mSetTime = time.hour + ":0" + time.minute;
                        else mSetTime = time.hour + ":" + time.minute;
                    if(time.hour < 10) mSetTime = "0" + mSetTime;
                }
                contentValues.put(DatabaseHelper.COLUMN_TIME, mSetTime);
                contentValues.put(DatabaseHelper.COLUMN_REPEAT, mChooseDays.toString());


                Intent intent = getIntent();
                String timeString = intent.getStringExtra("Time");
                Log.i("InSet", timeString);
                if(timeString.equals("CreateNew")) {
                    contentValues.put(DatabaseHelper.COLUMN_STATUS, "1");
                    mDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                    mDatabase.close();
                }else{
                    contentValues.put(DatabaseHelper.COLUMN_STATUS, "0");
                    int update = mDatabase.update(DatabaseHelper.TABLE_NAME, contentValues,
                            DatabaseHelper.COLUMN_TIME + "= ?", new String[]{ timeString});
                }
                finish();
                break;
            case R.id.Cancel:
                mDatabase.close();
                finish();
                break;
            case R.id.repeat:
                mChooseDays = new ArrayList<Integer>();
                data = getResources().getStringArray(R.array.days);
                checkedDays= new boolean[data.length];
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
                mBuilder.setTitle(R.string.repeat);
                mBuilder.setMultiChoiceItems(data, checkedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if(isChecked) mChooseDays.add(which);
                        else mChooseDays.remove(Integer.valueOf(which));
                    }
                });

                mBuilder.setCancelable(true);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                break;
            default:
                break;
        }

    }

}

