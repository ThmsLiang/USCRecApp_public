package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import uk.co.jakebreen.sendgridandroid.SendGrid;
import uk.co.jakebreen.sendgridandroid.SendGridMail;
import uk.co.jakebreen.sendgridandroid.SendGridResponse;
import uk.co.jakebreen.sendgridandroid.SendTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    String USCIDNumber;
    EditText editTextEmail, editTextPassword;
    ProgressBar progressBar;

    public static AlarmManager alarmManager;
    public static String CHANNEL_ID = "alarm_channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        findViewById(R.id.textViewSignup).setOnClickListener(this);
        findViewById(R.id.buttonLogin).setOnClickListener(this);

    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!isEmailValid(email,editTextEmail) || !isPasswordValid(password,editTextPassword))
            return;
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    USCIDNumber = mAuth.getCurrentUser().getDisplayName();
                    finish();
                    Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.toastForLoginFailure,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static boolean isEmailValid(String email, EditText emailEdit)
    {
        if (email == null || emailEdit == null)
            return false;

        if(email.isEmpty()) {
            emailEdit.setError("Email is required");
            emailEdit.requestFocus();
            return false;

        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Please enter a valid email");
            emailEdit.requestFocus();
            return false;

        }

        else if (!email.endsWith("@usc.edu")) {
            emailEdit.setError("Please enter a USC email address");
            emailEdit.requestFocus();
            return false;

        }

        return true;
    }

    public static boolean isPasswordValid(String password, EditText passEdit)
    {

        if (password == null || passEdit == null)
            return false;

        if (password.isEmpty()) {
            passEdit.setError("Password is required");
            passEdit.requestFocus();
            return false;

        }

        else if (password.length() < 6) {
            passEdit.setError("Password length must be at least 6");
            passEdit.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.textViewSignup) {
            finish();
            startActivity(new Intent(this,SignUpActivity.class));
        }
        else if (view.getId() == R.id.buttonLogin) {
            userLogin();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}