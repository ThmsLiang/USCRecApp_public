package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;


public class MapActivity extends AppCompatActivity {
    private static ArrayList<RecCenter> recCentersList;
    private static User user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        //get user info
        //TODO

        //show upcoming appointments on the corner
        ArrayList<String> upcomings = new ArrayList<>();
        for(Appointment appointment: user.getUpcoming()){
            upcomings.add(appointment.toString());
        }

        //Design a adaptor
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.map_listview, upcomings);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // Create a click handler
        listView.setOnItemClickListener(messageClickedHandler);

    }
    public void displayView(){}
    public void displayRec(){}
    public void displayRecCenter(){}
    // Create a message handling object as an anonymous class.
    private AdapterView.OnItemClickListener messageClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // Do something in response to the click
            onClickWindow(v);
        }
    };


    public void onClickRecCenter(View view, RecCenter recCenter){
        Intent intent = new Intent(this, BookingPageActivity.class);
        intent.putExtra("RecCenter", recCenter);
        startActivity(intent);
    }
    public void onClickWindow(View view){
        Intent intent = new Intent(this, SummaryPageActivity.class);
        startActivity(intent);
    }
}
