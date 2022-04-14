package com.example.myapplication;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SummaryPageActivity extends AppCompatActivity {
    private ArrayList<Appointment> myAppointments;
    String USCIDNumber;
    FirebaseAuth mAuth;
    public static final String TAG = "Firebase Message: ";


    public void displayView(){}
    public void onClickReturn(){}
    public void onClickDelete(View view){}

    public ArrayList<Appointment> getMyAppointments() {
        return myAppointments;
    }

    public void setMyAppointments(ArrayList<Appointment> myAppointments) {
        this.myAppointments = myAppointments;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_page);

        // initialize the database if it's uninitialized yet
        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        // enable tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get firebase instance
        mAuth = FirebaseAuth.getInstance();
        if (mAuth == null) Log.d(TAG,"Auth instance is null");

        //get current User info - appointments
        FirebaseUser myUser = mAuth.getCurrentUser();

        if (myUser == null) Log.d(TAG,"null user");

        USCIDNumber = myUser.getDisplayName();
        if (USCIDNumber == null) Log.d(TAG,"USCIDNumber is null");

        Log.d(TAG,USCIDNumber);
        DocumentReference userRef = Database.db.collection("User").document(USCIDNumber);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d(TAG,"DocumentSnapshot data: " + doc.getData());
                        ArrayList<Object> apptArray = (ArrayList<Object>) doc.get("Appointments");


                        // fetch and store all the data fields inside multiple arrays
                        ArrayList<String> recCenterNames = new ArrayList<>(), dates = new ArrayList<>(),
                                times = new ArrayList<>(), prevOrCurrents = new ArrayList<>();
                        ArrayList<Timestamp> timestamps = new ArrayList<>();
                        ArrayList<Integer> indices = new ArrayList<>();

                        if (!appointmentDataParse(apptArray,recCenterNames,dates,times,prevOrCurrents,timestamps,indices)) {
                            Log.d("Parsing Debug","apptArray was invalid");
                        }


                        // construct a list of hashmap for the content of the listView
                        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
                        for(int i = 0; i < recCenterNames.size(); ++i) {
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("recCenterName", recCenterNames.get(i));
                            map.put("date", dates.get(i));
                            map.put("time", times.get(i));
                            map.put("prevOrCurrent", prevOrCurrents.get(i));
                            listItem.add(map);
                        }

                        // construct a simple adapter
                        SimpleAdapter mSimpleAdapter = new SimpleAdapter(SummaryPageActivity.this,
                                listItem,
                                R.layout.summary_page_row_items,
                                new String[]{"recCenterName", "date", "time","prevOrCurrent"},
                                new int[]{R.id.RecCenterName, R.id.dateSum, R.id.timeSum,R.id.prevOrCurrent});

                        // find the view, create an array adapter to display all the time slots
                        ListView listView = (ListView) findViewById(R.id.summaryPage);

                        // bind the adapter to the list view
                        listView.setAdapter(mSimpleAdapter);


                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (prevOrCurrents.get(i).equals("current appointment")) {
                                    Intent intent = new Intent(SummaryPageActivity.this, DeleteAppointmentActivity.class);
                                    intent.putExtra("DeleteNum", indices.get(i));
                                    intent.putExtra("RecCenter", recCenterNames.get(i));
                                    intent.putExtra("Date",dates.get(i));
                                    intent.putExtra("Time",times.get(i));
                                    intent.putExtra("Appointment", (Serializable) apptArray.get(indices.get(i)));
                                    intent.putExtra("Timestamp",timestamps.get(i).toString());

                                    SummaryPageActivity.this.finish();
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                    else {
                        Log.d(TAG,"No such doc");
                    }
                }
                else {
                    Log.d(TAG,"get failed with", task.getException());
                }
            }
        });


    }

    // enable the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(SummaryPageActivity.this,GMapsActivity.class);
                this.finish();
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // helper function for date calculation
    public static Date addHoursToJavaUtilDate(Date date, Long hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        try {
            calendar.add(Calendar.HOUR_OF_DAY, Math.toIntExact(hours));
        }
        catch(ArithmeticException e) {
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        return calendar.getTime();
    }

    public static boolean appointmentDataParse(ArrayList<Object> apptArray, ArrayList<String> recCenterNames,ArrayList<String> dates, ArrayList<String> times,
                                            ArrayList<String> prevOrCurrents, ArrayList<Timestamp> timestamps, ArrayList<Integer> indices)
    {
        boolean ret = true;
        if (apptArray == null)
            return false;

        int count = 0;
        for (Object o : apptArray) {
            if (!(o instanceof Map)) {
                //Log.d(TAG,"invalid appointment");
                count++;
                ret = false;
                continue;
            }

            Map appt = (Map) o;
            if (!(appt.get("successfullyBooked") instanceof Boolean)) {
                //Log.d(TAG,"missing successfullyBooked: skipping appt");
                count++;
                ret = false;
                continue;
            }

            Boolean successfullyBooked = (Boolean)appt.get("successfullyBooked");

            if (!successfullyBooked) {
                count++;
                continue;
            }

            if (!(appt.get("recCenterName") instanceof String)) {

                //Log.d(TAG,"missing recCenterName: skipping appt");
                count++;
                ret = false;
                continue;

            }
            String recCenterName = (String) appt.get("recCenterName");

            if (!(appt.get("timeInterval") instanceof Map)) {
                //Log.d(TAG,"missing timeInt: skipping appt");
                count++;
                ret = false;
                continue;
            }

            Map timeInt = (Map) appt.get("timeInterval");


            if (!(timeInt.get("date") instanceof Timestamp)) {
                //Log.d(TAG,"missing timeStamp: skipping appt");
                count++;
                ret = false;
                continue;
            }

            Timestamp timeStamp = (Timestamp) timeInt.get("date");
            timestamps.add(timeStamp);


            indices.add(count);
            recCenterNames.add(recCenterName);
            Date apptDate = timeStamp.toDate();

            Date currentDate = new Date();
            String prevOrCurr = (apptDate.compareTo(currentDate) > 0) ? "current" : "previous";
            boolean isCurr = prevOrCurr.equals("current");
            prevOrCurr += " appointment";
            prevOrCurrents.add(prevOrCurr);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = "Date: " + dateFormat.format(apptDate);

            dates.add(strDate);

            DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
            Date endDate = addHoursToJavaUtilDate(apptDate, (Long) timeInt.get("duration"));

            String strTime = "Time: " + timeFormat.format(apptDate) + " - " +timeFormat.format(endDate);
            times.add(strTime);
           // Log.d(TAG,"RecCenter Name: " + recCenterName + "\ndate: " + strDate + "\ntime: " + strTime + "\nprevOrCurr: " + prevOrCurr);
            count++;
        }

        return ret;
    }

}
