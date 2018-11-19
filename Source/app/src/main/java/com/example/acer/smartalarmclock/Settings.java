package com.example.acer.smartalarmclock;

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
            }
        });
        mDatabase = mHelper.getWritableDatabase();

    }


    public void onButtonClick(View v){
        final ContentValues contentValues = new ContentValues();
        switch (v.getId()){
            case R.id.Save:
                contentValues.put(DatabaseHelper.COLUMN_TIME, mSetTime);
                Log.i("InSet", Integer.toString(mChooseDays.size()));
                if(mChooseDays.size() != 0 || mChooseDays == null) contentValues.put(DatabaseHelper.COLUMN_REPEAT, mChooseDays.toString());
                else {

                    mDatabase.close();
                    finish();

                }

                Intent intent = getIntent();
                String timeString = intent.getStringExtra("Time");
                Log.i("InSet", timeString);
                if(timeString.equals("CreateNew")) {

                    mDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                    mDatabase.close();
                }else{
                    int update = mDatabase.update(DatabaseHelper.TABLE_NAME, contentValues,
                            DatabaseHelper.COLUMN_TIME + "= ?", new String[]{ timeString});
                    Log.i("InSet", "Update " + Integer.toString(update));
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

