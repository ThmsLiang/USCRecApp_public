package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;

public class DeleteAppointmentActivity extends AppCompatActivity {
    String recCenterName, currDate, currTime, currPrevOrCurr, USCIDNumber, timestamp;

    int delNum;
    Serializable appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_appointment);

        if(Database.db == null) {
            Database.db = FirebaseFirestore.getInstance();
        }

        // enable tool bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent != null) {
            delNum = (int) intent.getSerializableExtra("DeleteNum");
            recCenterName = (String) intent.getSerializableExtra("RecCenter");
            currDate = (String) intent.getSerializableExtra("Date");
            currTime = (String) intent.getSerializableExtra("Time");
            currPrevOrCurr = (String) intent.getSerializableExtra("prevOrCurrent");
            appointment = (Serializable) intent.getSerializableExtra("Appointment");
            timestamp = (String) intent.getSerializableExtra("Timestamp");

        }
        LinearLayout llDel = findViewById(R.id.linearLayoutDel);
        TextView textView = new TextView(this);
        textView.setText("Rec Center Name: " + recCenterName + "\n" + currDate + "\n" + currTime);
        textView.setTextSize(16);
        textView.setTextAlignment(textView.TEXT_ALIGNMENT_CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        textView.setLayoutParams(params);
        llDel.addView(textView);

        findViewById(R.id.delButton).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                USCIDNumber = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                DocumentReference userRef = Database.db.collection("User").document(USCIDNumber);
                Map appt = (Map) appointment;
                Map tInt = (Map) appt.get("timeInterval");

                userRef.update("Appointments", FieldValue.arrayRemove(appointment)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Deletion message: ","delete was successful...notifying those on waitlist");
                            Toast.makeText(getApplicationContext(),"successfully deleted appointment",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Log.d("Deletion message: ",task.getException().getMessage() + "\n\n\n",task.getException());
                        }
                    }
                });
                DocumentReference recRef = Database.db.collection("RecCenter").document(recCenterName);

                recRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Log.d("Database stuff","RecCenter data: " + doc.getData());
                                ArrayList<Map> timeSlots = (ArrayList<Map>) doc.get("timeSlots");

                                ArrayList<String> emailWaitingList = new ArrayList<>();
                                Long currRegistered = null;
                                Map timeSlotToUpdate = null;

                                for (Map timeslot : timeSlots) {
                                    if (timeslot.get("date").toString().equals(timestamp)) {
                                        emailWaitingList = (ArrayList<String>) timeslot.get("waitingList");
                                        currRegistered = (Long) timeslot.get("currentRegistered");
                                        timeSlotToUpdate = timeslot;

                                    }

                                }

                                if (timeSlotToUpdate != null) {
                                    recRef.update("timeSlots", FieldValue.arrayRemove(timeSlotToUpdate));
                                    timeSlotToUpdate.put("currentRegistered",currRegistered - 1);
                                    timeSlotToUpdate.put("waitingList",new ArrayList<>());
                                    recRef.update("timeSlots",FieldValue.arrayUnion(timeSlotToUpdate));
                                }
                                else
                                    Log.d("Database stuff", "could not find timeslot");

                                SendGrid sendGrid = SendGrid.create("INPUT_YOUR_SENDDRID_API_KEY_HERE");

                                SendGridMail testMail = new SendGridMail();
                                testMail.setFrom("elimorri@usc.edu",null);
                                String subject = "Appointment Availability";
                                testMail.setSubject(subject);
                                emailWaitingList.forEach(x->testMail.addRecipientBlindCarbonCopy(x,null));
                                testMail.addRecipient("elimorri@usc.edu",null);
                                testMail.setContent("An availability has opened up for your requested timeslot.");
                                Single.fromCallable(sendGrid.send(testMail))
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(response -> {
                                            if (response.isSuccessful()) {
                                                Log.d("SendGrid","Response successful");
                                            }
                                            else {
                                                Log.d("SendGrid",response.getErrorMessage());
                                            }
                                        });

                            }
                            else {
                                Log.d("Database stuff","no such recCenter doc");
                            }
                        }
                        else {
                            Log.d("Database stuff","error retrieving recCenter doc");
                        }
                    }
                });


                DeleteAppointmentActivity.this.finish();
                Intent intent = new Intent(DeleteAppointmentActivity.this,SummaryPageActivity.class);
                startActivity(intent);
            }
        });
    }

    // enable the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent(DeleteAppointmentActivity.this,SummaryPageActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}