package com.example.acer.smartalarmclock;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Button mSave;
    private Button mCancel;
    private Button mRepeat;
    private ArrayList<Integer> mChooseDays;
    private String[] data;
    private boolean[] checkedDays;
    private int mSetTime;
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
        mRepeat = (Button)findViewById(R.id.repeat);
        mCancel = (Button)findViewById(R.id.Cancel);
        mSave = (Button)findViewById(R.id.Save);

        mTimePicker.setCurrentHour(time.hour);
        mTimePicker.setCurrentMinute(time.minute);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mSetTime = hourOfDay * 60 + minute;
            }
        });
    }


    public void onButtonClick(View v){
        mDatabase = mHelper.getWritableDatabase();
        final ContentValues contentValues = new ContentValues();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentValues.put(DatabaseHelper.COLUMN_TIME, mSetTime);
                contentValues.put(DatabaseHelper.COLUMN_REPEAT, mChooseDays.toString());
                mDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
                mDatabase.close();
                finish();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

}

