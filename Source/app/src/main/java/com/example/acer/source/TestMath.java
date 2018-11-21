package com.example.acer.source;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class TestMath extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_math);

        Random random = new Random();
        int first = random.nextInt(30);
        int second = random.nextInt(first);
        TextView example = (TextView)findViewById(R.id.example);
        example.setText(new String(Integer.toString(first)) + " - " + Integer.toString(second) + " = ?");
        Button[] buttons = new Button[3];
        buttons[0] = (Button)findViewById(R.id.res0);
        buttons[1] = (Button)findViewById(R.id.res1);
        buttons[2] = (Button)findViewById(R.id.res2);

        ArrayList<String> res = new ArrayList<String>();
        res.add(Integer.toString(first - second));
        res.add(Integer.toString(first - random.nextInt(second)));
        res.add(Integer.toString(second + random.nextInt(6)));

        for(int i = 0; i < 3; i++){
            int pointer = random.nextInt(2 - i);
            buttons[i].setText(res.get(pointer));
            res.remove(pointer);
        }



    }
}
