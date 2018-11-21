package com.example.acer.source;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.text.format.Time;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AlarmClock extends AppCompatActivity {
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDataBase;
    private ListView mAlarmsList;
    private Switch mIsSetAlarmSwitch;


    private String mCurrentTime;
    private ArrayList<String> mTimes;
    private boolean[] mIsSetAlarm;
    private ArrayList<String> mDays;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlarmsList = (ListView)findViewById(R.id.alarmsList);
        mTimes = new ArrayList<String>();
        mDays = new ArrayList<String>();
        mIsSetAlarmSwitch = findViewById(R.id.isSet);

        setInformation();

        Time now = new Time();
        now.setToNow();




        mAlarmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object data = parent.getItemAtPosition(position);

                
                Log.i("Switch", "State: " + data);

                Intent intent = new Intent(".Settings");
                ListAdapter adapter = mAlarmsList.getAdapter();

                String a = adapter.getItem(position).toString();
                mCurrentTime =  a.substring(a.indexOf("Time=")+5, a.indexOf("Time=")+10);
                intent.putExtra("Time", mCurrentTime);
                startActivity(intent);


            }
        });

        mAlarmsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListAdapter adapter = mAlarmsList.getAdapter();
                String a = adapter.getItem(position).toString();
                mCurrentTime =  a.substring(a.indexOf("Time=")+5, a.indexOf("Time=")+10);

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(AlarmClock.this);
                mBuilder.setTitle("Delete alarm");
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDataBase.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_TIME + " =?", new String[]{mCurrentTime} );
                        setInformation();
                    }
                });
                mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mBuilder.show();
                return true;
            }
        });




    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setInformation();
        setAlarm();
    }

    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.addButton:
                Intent intent = new Intent(".Settings");
                intent.putExtra("Time", "CreateNew");
                startActivity(intent);
                //mDataBase.delete(DatabaseHelper.TABLE_NAME, null, null);
                break;

            default:
                break;
        }
    }

    public void setInformation(){
        mTimes.clear();
        mDatabaseHelper = new DatabaseHelper(this);
        mDataBase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mDataBase.query(DatabaseHelper.TABLE_NAME, null, null,
                null, null, null, null);

        if(cursor.moveToFirst()){
            String[] repeatDays = new String[cursor.getCount()];
            String[] times = new String[cursor.getCount()];
            boolean[] status  =new boolean[cursor.getCount()];

            int count = 0;
            int timeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME);
            int repeatIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_REPEAT);
            int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);

            do{
                times[count] = cursor.getString(timeIndex);

                String st = cursor.getString(statusIndex);
                if(st.equals("1")) status[count] = true;
                    else status[count] = false;

                if(st.equals("1")){
                    mTimes.add(times[count]);
                }
                String days = cursor.getString(repeatIndex);
                String temp = new String();
                for(int i = 0; i < days.length(); i++) {
                    switch (days.charAt(i)) {
                        case '0':
                            temp +="0";
                            repeatDays[count] += "Mon";
                            break;
                        case '1':
                            temp +="1";
                            repeatDays[count] += "Tue";
                            break;
                        case '2':
                            temp +="2";
                            repeatDays[count] += "Wed";
                            break;
                        case '3':
                            temp +="3";
                            repeatDays[count] += "Thu";
                            break;
                        case '4':
                            temp +="4";
                            repeatDays[count] += "Fri";
                            break;
                        case '5':
                            temp +="5";
                            repeatDays[count] += "Sat";
                            break;
                        case '6':
                            temp +="6";
                            repeatDays[count] += "Sun";
                            break;
                        case ',':
                            repeatDays[count] += ", ";
                            break;
                        default:
                            break;

                    }

                }
                if(st.equals("1")) mDays.add(temp);
                repeatDays[count] = repeatDays[count].substring(4);
                count++;
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                    times.length);
            Map<String, Object>  map;

            for(int i = 0; i < times.length; i++){
                map = new HashMap<String, Object>();
                map.put("Time", times[i]);
                map.put("Days", repeatDays[i]);
                map.put("Status", status[i]);
                data.add(map);
                Log.i("MLog", "Id: " + i + "Time: " + times[i]+ "Days: " + repeatDays[i]
                        + "Sta: " + status[i]);
            }

            mIsSetAlarm = status.clone();
            SimpleAdapter adapter = new SimpleAdapter(this, data,
                    R.layout.list_alarms, new String[]{"Time", "Days", "Status"},
                    new int[] {R.id.textAlarm, R.id.textDays, R.id.isSet});
            mAlarmsList.setAdapter(adapter);
            for(int i = 0; i < mTimes.size(); i++){
                Log.i("MLogRes", mTimes.get(i) );
                Log.i("MLogRes", mDays.get(i) );
            }
        }

    }
    private void setAlarm(){
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent;
        PendingIntent pendingIntent;

        intent = new Intent(this, Alert.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        for(int i = 0; i < mTimes.size(); i++){
            String time = mTimes.get(i);

            int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
            Log.i("AlarmTest", "Hour: "+hour);
            int minute = Integer.parseInt(time.substring(time.indexOf(':')+1, time.length()));
            Log.i("AlarmTest", "Minute: "+hour);

            Time now = new Time();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, now.hour);
            calendar.set(Calendar.MINUTE, now.minute);
            calendar.set(Calendar.SECOND, now.second);

            int setHour = hour - now.hour;
            int setMin = minute - now.minute;

            int sec = now.second;
            long alarmTime = ((setHour*60 + (setMin - 1))*60 + (60 - sec)) * 1000;

            Log.i("TimeSet", "Time: " +alarmTime);
            Log.i("TimeSet", "TimeReal: " +SystemClock.elapsedRealtime());
            if(alarmTime > 0)
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                        + alarmTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }
}
