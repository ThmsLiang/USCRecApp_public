package com.example.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TimeSlotActivity extends AppCompatActivity {
    RecCenter currRecCenter;
    TimeSlot currTimeSlot;
    User currentUser;
    ArrayList<HashMap<String, Object>> appointments;
    boolean dataUpdate;

    public static int requestCode;
    public static ArrayList<Date> myDates = new ArrayList<>();
    public static long timeBeforeAppointment = 1000 * 60 * 30; //30 min before appt by default
    public static PendingIntent pendingIntent;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        dataUpdate = false;

        // initialize the database if it's uninitialized yet
        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        // map to the booking page layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_slot_detail);

        // enable tool bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // get the current user
        currentUser = new User();
        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
            currentUser.setUSCID(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            currentUser.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        Intent intent = getIntent();
        if(intent != null) {
            currTimeSlot = (TimeSlot) intent.getSerializableExtra("TimeSlot");
            currRecCenter = (RecCenter) intent.getSerializableExtra("RecCenter");
            appointments = (ArrayList<HashMap<String, Object>>) intent.getSerializableExtra("Appointments");

        }

        // set the visibility of the remind me button
        Button remindMe = findViewById(R.id.remindMe);
        Button reserve = findViewById(R.id.reserve);
        if(currTimeSlot.capacity > currTimeSlot.currentRegistered) {
            remindMe.setEnabled(false);
            reserve.setEnabled(true);
        } else {
            remindMe.setEnabled(true);
            reserve.setEnabled(false);
        }


        DocumentReference userRef2 = Database.db.collection("User").document(currentUser.getUSCID());

        userRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.d("debug info", "DocumentSnapshot data: " + doc.getData());
                        ArrayList<Object> apptArray = (ArrayList<Object>) doc.get("Appointments");
                        if (apptArray != null) {
                            requestCode = apptArray.size() - 1;
                        } else {
                            requestCode = 0;
                        }
                    }
                    else {
                        requestCode = 0;
                    }
                }
                else {
                    requestCode = 0;
                }
            }
        });

        // set on click activity
        remindMe.setOnClickListener((View view) -> {
            // we disallow a user to have multiple reservations/ reminders from the same rec center
            if(checkNoDuplicate()) {
                // update the recCenter's data
                DocumentReference recCenterRef = Database.db.collection("RecCenter").document(currRecCenter.getName());
                recCenterRef.update("timeSlots", FieldValue.arrayRemove(currTimeSlot));
                currTimeSlot.addToWaitingList(currentUser);
                recCenterRef.update("timeSlots", FieldValue.arrayUnion(currTimeSlot));

                // add the appointment to the user's record
                Appointment appointment = new Appointment();
                appointment.setRecCenterName(currRecCenter.getName());
                appointment.setTimeInterval(currTimeSlot);
                appointment.setSuccessfullyBooked(false);

                DocumentReference userRef = Database.db.collection("User").document(currentUser.getUSCID());
                userRef.update("Appointments", FieldValue.arrayUnion(appointment)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.reminderSucceed, Snackbar.LENGTH_SHORT);
                        success.show();
                        dataUpdate = true;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.reminderFailed, Snackbar.LENGTH_SHORT);
                        success.show();
                    }
                });
            } else {
                Snackbar warning = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.duplicateWarning, Snackbar.LENGTH_SHORT);
                warning.show();
            }
        });

        // callback function for the reserve button
        reserve.setOnClickListener((View view) -> {
            // we disallow a user to have multiple reservations/ reminders from the same rec center
            if(checkNoDuplicate()) {
                // update the recCenter's data
                DocumentReference recCenterRef = Database.db.collection("RecCenter").document(currRecCenter.getName());
                recCenterRef.update("timeSlots", FieldValue.arrayRemove(currTimeSlot));
                int registerNum = currTimeSlot.getCurrentRegistered();
                currTimeSlot.setCurrentRegistered(registerNum + 1);
                recCenterRef.update("timeSlots", FieldValue.arrayUnion(currTimeSlot));

                // add the appointment to the user's record
                Appointment appointment = new Appointment();
                appointment.setRecCenterName(currRecCenter.getName());
                appointment.setTimeInterval(currTimeSlot);
                appointment.setSuccessfullyBooked(true);

                DocumentReference userRef = Database.db.collection("User").document(currentUser.getUSCID());
                userRef.update("Appointments", FieldValue.arrayUnion(appointment)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Appointment Debug","Appointment Success");
                        Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.appointmentSucceed, Snackbar.LENGTH_SHORT);
                        success.show();
                        dataUpdate = true;
                        Date myDate = currTimeSlot.getDate();
                        myDates.add(myDate);
                        Date appointmentDate = new Date(myDate.getTime() - timeBeforeAppointment);
                        createNotification(appointmentDate);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Appointment Debug","failed to add appointment",e);
                        Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.appointmentFailed, Snackbar.LENGTH_SHORT);
                        success.show();
                    }
                });
            } else {
                Snackbar warning = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.duplicateWarning, Snackbar.LENGTH_SHORT);
                warning.show();
            }
        });


    }

    // enable the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(dataUpdate) {
                    dataUpdate = false;
                    onBackPressed();
                } else {
                    this.finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // the back button should go directly to the map, so when the user enter the booking page again it will get refreshed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TimeSlotActivity.this, GMapsActivity.class));
        finish();
    }

    // check if the user already have a reservation or reminder on that rec center
    // if so, disable the button
    // returns true if there's no duplicates
    boolean checkNoDuplicate() {
        if(!appointments.isEmpty()) {
            for (int i = 0; i < appointments.size(); ++i) {
                String first = (String)appointments.get(i).get("recCenterName");
                String second = (String)currRecCenter.getName();
                if(first != null && second != null) {
                    if (first.equals(second)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void createNotification(Date notifyTime) {
        Log.d("Debugging notifications:","entered createNotification");
        Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
        intent.setData(Uri.parse("custom://" + requestCode));
        intent.setAction(String.valueOf(requestCode));


        PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
        pendingIntent = broadcast;
        MainActivity.alarmManager.setExact(AlarmManager.RTC_WAKEUP,notifyTime.getTime(), broadcast);
        requestCode++;

        Log.d("Notification set:",notifyTime.toString());
        Log.d("Notification set:","Used requestCode: " + requestCode);

    }

}
