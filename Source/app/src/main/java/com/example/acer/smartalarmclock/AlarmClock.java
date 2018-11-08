package com.example.acer.smartalarmclock;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.text.format.Time;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class AlarmClock extends AppCompatActivity {
    private Button mAddAlarm;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDataBase;
    private ListView mAlarmsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Time now = new Time();
        now.setToNow();

        mAddAlarm = (Button)findViewById(R.id.addButton);

        setInformation();
    }
    public void onButtonClick(View v){
        mAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(".Settings");
                startActivity(intent);

            }
        });

    }

    private void setInformation(){
        mAlarmsList = (ListView)findViewById(R.id.alarmsList);
        mDatabaseHelper = new DatabaseHelper(this);
        mDataBase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mDataBase.query(DatabaseHelper.TABLE_NAME, null, null,
                null, null, null, null);

        if(cursor.moveToFirst()){
            String[] repeatDays = new String[cursor.getCount()];
            String[] times = new String[cursor.getCount()];
            int count = 0;
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int timeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME);
            int repeatIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_REPEAT);

            do{
                int hour = cursor.getInt(timeIndex)/60;
                int minute = cursor.getInt(timeIndex) - hour * 60;
                if(minute < 10) times[count] = new String(hour + ":0" + minute);
                else times[count] = new String(hour + ":" + minute);

                String days = cursor.getString(repeatIndex);
                for(int i = 0; i < days.length(); i++) {
                    char c = days.charAt(i);
                    switch (days.charAt(i)) {
                        case '0':
                            repeatDays[count] += "Mon";
                            break;
                        case '1':
                            repeatDays[count] += "Tues";
                            break;
                        case '2':
                            repeatDays[count] += "Wed";
                            break;
                        case '3':
                            repeatDays[count] += "Thu";
                            break;
                        case '4':
                            repeatDays[count] += "Fri";
                            break;
                        case '5':
                            repeatDays[count] += "Sat";
                            break;
                        case '6':
                            repeatDays[count] += "Sun";
                            break;
                        case ',':
                            repeatDays[count] += ", ";
                            break;
                         default:
                             break;

                    }

                }
                repeatDays[count] = repeatDays[count].substring(4);
                count++;
            }while(cursor.moveToNext());

            ArrayList<HashMap<String, String>> arrayList  =new ArrayList<>();
            HashMap<String, String>  map;


            for(int i = 0; i < times.length; i++){
                map = new HashMap<>();
                map.put("Time", times[i]);
                map.put("Days", repeatDays[i]);
                arrayList.add(map);
            }


            SimpleAdapter adapter = new SimpleAdapter(this, arrayList,
             android.R.layout.simple_list_item_2, new String[]{"Time", "Days"},
                      new int[] {android.R.id.text1, android.R.id.text2});
            mAlarmsList.setAdapter(adapter);
        }

    }


}
