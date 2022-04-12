package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeSlotActivity extends AppCompatActivity {
    RecCenter currRecCenter;
    TimeSlot currTimeSlot;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize the database if it's uninitialized yet
        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        // map to the booking page layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_slot_detail);

        // enable tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add a test user
        currentUser = new User();
        currentUser.userName = "Thomas";
        currentUser.USCID = "1234567890";
        currentUser.Appointments = new ArrayList<>();
        currentUser.setUSCID(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        Intent intent = getIntent();
        if(intent != null) {
            currTimeSlot = (TimeSlot) intent.getSerializableExtra("TimeSlot");
            currRecCenter = (RecCenter) intent.getSerializableExtra("RecCenter");
        }

        // set the visibility of those two buttons
        Button remindMe = findViewById(R.id.remindMe);
        Button reserve = findViewById(R.id.reserve);
        if(currTimeSlot.isAvailable()) {
            remindMe.setEnabled(false);
            reserve.setEnabled(true);
        } else {
            remindMe.setEnabled(true);
            reserve.setEnabled(false);
        }

        // set on click activity
        remindMe.setOnClickListener((View view) -> {
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
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.reminderFailed, Snackbar.LENGTH_SHORT);
                    success.show();
                }
            });

            // update the recCenter's data
            DocumentReference recCenterRef = Database.db.collection("RecCenter").document(currRecCenter.getName());
            recCenterRef.update("timeSlots",FieldValue.arrayRemove(currTimeSlot));

            // decrease the number of available spots of the current time slot
            currTimeSlot.addToWaitingList(currentUser);
            recCenterRef.update("timeSlots",FieldValue.arrayUnion(currTimeSlot));
        });

        // callback function for the reserve button
        reserve.setOnClickListener((View view) -> {
            // add the appointment to the user's record
            Appointment appointment = new Appointment();
            appointment.setRecCenterName(currRecCenter.getName());
            appointment.setTimeInterval(currTimeSlot);
            appointment.setSuccessfullyBooked(true);

            DocumentReference userRef = Database.db.collection("User").document(currentUser.getUSCID());
            userRef.update("Appointments", FieldValue.arrayUnion(appointment)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.appointmentSucceed, Snackbar.LENGTH_SHORT);
                    success.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar success = Snackbar.make(findViewById(R.id.timeSlotDetailView), R.string.appointmentFailed, Snackbar.LENGTH_SHORT);
                    success.show();
                }
            });

            // update the recCenter's data
            DocumentReference recCenterRef = Database.db.collection("RecCenter").document(currRecCenter.getName());
            recCenterRef.update("timeSlots",FieldValue.arrayRemove(currTimeSlot));

            // decrease the number of available spots of the current time slot
            currTimeSlot.setCurrentRegistered(currTimeSlot.getCurrentRegistered() + 1);
            recCenterRef.update("timeSlots",FieldValue.arrayUnion(currTimeSlot));
        });
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
