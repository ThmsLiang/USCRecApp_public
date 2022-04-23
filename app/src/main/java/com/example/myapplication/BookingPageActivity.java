package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.view.MenuItem;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BookingPageActivity extends AppCompatActivity {
    String currentLocationName;
    public static RecCenter currentLocation;
    User currentUser;
    String TAG = "Booking page info";
    public FirebaseAuth mAuth;
    HashMap<String, RecCenter> all_centers = new HashMap<>();
    ArrayList<HashMap<String, Object>> all_appointments;
    ArrayList<TimeSlot> timeSlots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // map to the booking page layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_page);

        // enable tool bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // get the rec center object from the intent
        Intent intent = getIntent();
        currentLocationName = intent.getStringExtra("RecCenter");

        // get the current user
        currentUser = new User();
        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
            currentUser.setUSCID(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }

        // fetch rec centers from firebase
        // initialize the database if it's uninitialized yet
        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        //get firebase instance
        mAuth = FirebaseAuth.getInstance();
        Database.db.collection("RecCenter")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                all_centers.put(document.getId(), document.toObject(RecCenter.class));
                                Log.d(TAG, document.getId() + " => " + all_centers.get(document.getId()));
                            }
                            getAppointments();
                        } else {
                            Log.d(TAG, "something went wrong when querying rec centers");
                        }
                    }
                });
    }

    // get all the appointments of the current user
    @SuppressWarnings("unchecked")
    public void getAppointments() {
        Database.db.collection("User").document(currentUser.getUSCID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            all_appointments = (ArrayList<HashMap<String, Object>>) document.get("Appointments");
                            generateView();
                        }
                    }
                });
    }

    public void generateView() {
        currentLocation = all_centers.get(currentLocationName);

        // fetch and store all the data fields inside multiple arrays
        if(currentLocation != null) {
            timeSlots = currentLocation.getTimeSlots();
        }
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

            String remainingSpots = "Remaining: " + (i.getCapacity() - i.currentRegistered);
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
                intent.putExtra("Appointments", all_appointments);
                startActivity(intent);
            }
        });
    }

    // helper function for date calculation
    public static Date addHoursToJavaUtilDate(Date date, int hours) {
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
