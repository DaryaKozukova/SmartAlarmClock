package com.example.acer.smartalarmclock;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.text.format.Time;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class AlarmClock extends AppCompatActivity {
    private Button mAddAlarm;
    private TextView textAlarm;
    private Switch isSetAlarm;
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
        mAlarmsList = (ListView)findViewById(R.id.alarmsList);
        textAlarm = (TextView)findViewById(R.id.textAlarm);


        mAlarmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(".Settings");
                ListAdapter adapter = mAlarmsList.getAdapter();
                TextView i = (TextView)findViewById(R.id.textAlarm);
                Log.i("ClickL", i.getText().toString());

                intent.putExtra("Time", i.getText());
                startActivity(intent
                );
            }
        });
        setInformation();

    }
    public void onButtonClick(View v){
        switch (v.getId()){
            case R.id.addButton:
                Intent intent = new Intent(".Settings");
                intent.putExtra("Time", "CreateNew");
                startActivity(intent);
                mDataBase.delete(DatabaseHelper.TABLE_NAME, null, null);

                break;
            default:
                break;
        }
    }


    public void setInformation(){

        mDatabaseHelper = new DatabaseHelper(this);
        mDataBase = mDatabaseHelper.getWritableDatabase();
        Cursor cursor = mDataBase.query(DatabaseHelper.TABLE_NAME, null, null,
                null, null, null, null);

        if(cursor.moveToFirst()){
            String[] repeatDays = new String[cursor.getCount()];
            String[] times = new String[cursor.getCount()];
            boolean[] status  =new boolean[cursor.getCount()];
            int count = 0;
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int timeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME);
            int repeatIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_REPEAT);
            int statusIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);
            if(statusIndex == 1) status[count] = true;
            else status[count] = false;
            do{
                Log.i("LogID", "ID " + Integer.toString(cursor.getInt(idIndex)));
                times[count] = cursor.getString(timeIndex);
                String days = cursor.getString(repeatIndex);
                for(int i = 0; i < days.length(); i++) {
                    char c = days.charAt(i);
                    switch (days.charAt(i)) {
                        case '0':
                            repeatDays[count] += "Mon";
                            break;
                        case '1':
                            repeatDays[count] += "Tue";
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
            ArrayList<HashMap<String, Boolean>> arrayStatus = new ArrayList<>();
            HashMap<String, String>  map;
            HashMap<String, Boolean>  mapStatus;


            for(int i = 0; i < times.length; i++){
                map = new HashMap<>();
                map.put("Time", times[i]);
                map.put("Days", repeatDays[i]);
                arrayList.add(map);

                mapStatus = new HashMap<String, Boolean>();
                mapStatus.put("Status", true);
                arrayStatus.add(mapStatus);

                Log.i("MLog", "Id" + i + "Time" + times[i]+ "Days" + repeatDays[i] + "Sta" + status[i]);
            }


            SimpleAdapter adapter = new SimpleAdapter(this, arrayList,
             R.layout.list_alarms, new String[]{"Time", "Days"},
                      new int[] {R.id.textAlarm, R.id.textDays});
//            SimpleAdapter adapter = new SimpleAdapter(this, arrayList, android.R.layout.simple_list_item_2,
//                    new String[]{"Time", "Days"},
//                    new int[]{android.R.id.text1, android.R.id.text2});
            mAlarmsList.setAdapter(adapter);

        }

    }


}
