package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Switch;

public class SettingActivity extends AppCompatActivity {
    static boolean wantNotification;
    Switch sw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sw = (Switch) findViewById(R.id.switch1);
        if (sw.isChecked()){
            wantNotification=true;
        }else {
            wantNotification=false;
        }
    }
}