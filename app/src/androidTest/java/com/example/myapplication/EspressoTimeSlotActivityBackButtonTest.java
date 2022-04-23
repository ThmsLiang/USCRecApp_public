package com.example.myapplication;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EspressoTimeSlotActivityBackButtonTest {
    @Rule
    public ActivityScenarioRule<MainActivity> MainActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException {
        onView(withId(R.id.editTextEmail)).perform(typeText("yuzhew@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextPassword)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        sleep();
        onView(withId(R.id.buttonContinue)).perform(click());
        sleep();
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("UAC Lap Swim"));
        marker.click();
        sleep();
    }

    @Test
    public void TimeSlotGoBackButtonWithUpdate_isCorrect() {
        onData(anything()).inAdapterView(withId(R.id.bookingPage)).atPosition(0).perform(click());
        sleep();
        onView(withId(R.id.reserve)).perform(click());
        onView(withId((R.id.remindMe))).perform(click());
        sleep();
        onView(withContentDescription("Navigate up")).perform(click());
        sleep();
    }

    private static void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
