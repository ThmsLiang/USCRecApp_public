package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.MenuItem;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingPageActivity extends AppCompatActivity {
    String currentLocationName;
    public static RecCenter currentLocation;
    ArrayList<TimeSlot> timeSlots;
    String TAG = "Booking page info";
    public FirebaseAuth mAuth;
    HashMap<String, RecCenter> all_centers;

    private boolean queryFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // map to the booking page layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);

        // enable tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the rec center object from the intent
        Intent intent = getIntent();
        currentLocationName = intent.getStringExtra("RecCenter");

        /**************fetch rec center from database******************/

        all_centers = new HashMap<>();

        // initialize the database if it's uninitialized yet
        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        //get firebase instance
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) Log.d(TAG,"Auth instance is null");

        Database.db.collection("RecCenter")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                all_centers.put(document.getId(), document.toObject(RecCenter.class));
                                Log.d(TAG, document.getId() + " => " + all_centers.get(document.getId()));
                            }
                            generateView();
                        } else {
                            Log.d(TAG, "fucked up");
                        }
                    }
                });
    }

    public void generateView() {
        currentLocation = all_centers.get(currentLocationName);

        // fetch and store all the data fields inside multiple arrays
        ArrayList<TimeSlot> timeSlots = currentLocation.getTimeSlots();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();
        ArrayList<String> availabilities = new ArrayList<>();

        for(TimeSlot i: timeSlots) {
            Date date = i.getDate();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = "Date: " + dateFormat.format(date);
            dates.add(strDate);

            DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
            Date endDate = addHoursToJavaUtilDate(date, 1);
            String strTime = "Time: " + timeFormat.format(date) + " - " +timeFormat.format(endDate);
            times.add(strTime);

            String remainingSpots = "Remaining: " + Integer.toString(i.getCapacity() - i.currentRegistered);
            availabilities.add(remainingSpots);
        }

        // construct a list of hashmap for the content of the listView
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for(int i = 0; i < timeSlots.size(); ++i) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("date", dates.get(i));
            map.put("time", times.get(i));
            map.put("available", availabilities.get(i));
            listItem.add(map);
        }

        // construct a simple adapter
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem,
                R.layout.booking_page_row_items,
                new String[]{"date", "time", "available"},
                new int[]{R.id.date, R.id.time, R.id.availability});

        // find the view, create an array adapter to display all the time slots
        ListView listView = (ListView) findViewById(R.id.bookingPage);

        // bind the adapter to the list view
        listView.setAdapter(mSimpleAdapter);

        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(BookingPageActivity.this, TimeSlotActivity.class);
                intent.putExtra("TimeSlot", timeSlots.get(i));
                intent.putExtra("RecCenter", currentLocation);
                startActivity(intent);
            }
        });
    }

    // helper function for date calculation
    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    // enable the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
