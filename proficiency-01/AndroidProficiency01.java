package com.example.joshu.capstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.Timestamp;

public class AndroidProficiency01 extends AppCompatActivity {
    TextView tv = (TextView)findViewById(R.id.test);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_proficiency01);

        Long tsLong = System.currentTimeMillis();
        Timestamp t = new Timestamp(tsLong);

        String tsString = t.toString();
        String sHour = tsString.substring(11, 13);
        int hour = Integer.parseInt(sHour);


        if (hour >= 6 && hour < 12) {
            tv.setText("Good Morning!");
        } else if (hour >= 12 && hour < 19) {
            tv.setText("Good Afternoon!");
        } else {
            tv.setText("Good Evening!");
        }
    }

}




