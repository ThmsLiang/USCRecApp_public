package com.example.myapplication;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import android.content.Context;
import android.content.Intent;
import android.provider.Contacts;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SdkSuppress;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class GMapsUITest {
    private static final String BASIC_SAMPLE_PACKAGE
            = "com.example.myapplication";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen(){
        // Initialize UiDevice instance
        device = UiDevice.getInstance(getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);

        //Input email and password
        try {
            new UiObject(new UiSelector().description("email")).setText("elim@usc.edu");
            new UiObject(new UiSelector().description("password")).setText("123456");
            new UiObject(new UiSelector().description("signin")).click();
            new UiObject(new UiSelector().description("continue")).click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLyonMarker(){
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("Lyon Center"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCromwellMarker(){
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("Cromwell Track"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUACMarker(){
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("UAC Lap Swim"));
        try {
            marker.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCheckAppointmentButton(){
        UiDevice mDevice = UiDevice.getInstance(getInstrumentation());
        UiObject button = mDevice.findObject(new UiSelector().descriptionContains("checkAppointment"));
        try {
            button.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

}
