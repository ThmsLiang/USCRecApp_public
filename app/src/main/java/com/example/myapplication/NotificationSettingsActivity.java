package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NotificationSettingsActivity extends AppCompatActivity {

    private static int requestCode;
    private static String USCID;
    private static AlarmManager alarmManager;
    private static EditText editTextTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        editTextTime = (EditText) findViewById(R.id.editTextTime);


        Button deleteButton = (Button) findViewById(R.id.delNotificationButton);
        Button updateButton = (Button) findViewById(R.id.updateNotificationButton);


        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {
            USCID = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        else {
            USCID = "";
        }
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DocumentReference userRef2 = Database.db.collection("User").document(USCID);

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

        deleteButton.setOnClickListener((View view) -> {
            for (int i = requestCode; i >= 0; i--) {
                Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
                intent.setData(Uri.parse("custom://" + requestCode));
                intent.setAction(String.valueOf(requestCode));
                PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(),0,intent, PendingIntent.FLAG_MUTABLE);
              MainActivity.alarmManager.cancel(broadcast);
              broadcast.cancel();
                Log.d("Alarm Deleted","Deleted alarm: " + requestCode);
            }

            Toast.makeText(view.getContext(),"Notifications Deleted",Toast.LENGTH_SHORT).show();

        });

        updateButton.setOnClickListener((View view) -> {
            String timeString = editTextTime.getText().toString().trim();
            Pattern pattern = Pattern.compile("([2][0-3]|[0-1][0-9]|[1-9]):[0-5][0-9]:([0-5][0-9]|[6][0])");
            if (!pattern.matcher(timeString).matches()) {
                editTextTime.setError("Please enter a valid time");
                editTextTime.requestFocus();
            }

            String[] timeSplit = timeString.split(":");
            int hours = Integer.parseInt(timeSplit[0]);
            int minutes = Integer.parseInt(timeSplit[1]);
            int seconds = Integer.parseInt(timeSplit[2]);
            long millis = ((hours * 3600) + (minutes * 60) + seconds) * 1000;

            DocumentReference userRef = Database.db.collection("User").document(USCID);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            Log.d("debug message", "DocumentSnapshot data: " + doc.getData());
                            ArrayList<Object> apptArray = (ArrayList<Object>) doc.get("Appointments");

                            // fetch and store all the data fields inside multiple arrays
                            ArrayList<String> recCenterNames = new ArrayList<>(), dates = new ArrayList<>(),
                                    times = new ArrayList<>(), prevOrCurrents = new ArrayList<>();
                            ArrayList<Timestamp> timestamps = new ArrayList<>();
                            ArrayList<Integer> indices = new ArrayList<>();

                            if (!SummaryPageActivity.appointmentDataParse(apptArray,recCenterNames,dates,times,prevOrCurrents,timestamps,indices)) {
                                Log.d("Parsing Debug","apptArray was invalid");
                            }

                            for (int i = 0; i < apptArray.size(); i++) {

                                Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
                                intent.setData(Uri.parse("custom://" + requestCode));
                                intent.setAction(String.valueOf(requestCode));
                                PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(),0,intent, PendingIntent.FLAG_MUTABLE);

                                MainActivity.alarmManager.set(AlarmManager.RTC_WAKEUP,timestamps.get(i).toDate().getTime() - millis, broadcast);
                            }

                            Toast.makeText(view.getContext(),"Notifications Updated",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });



        });
    }
}