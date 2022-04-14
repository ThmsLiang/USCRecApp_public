package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoBookingPageActivityOnCreateTest {
    @Rule
    public ActivityScenarioRule<MainActivity> MainActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);

    @Before
    public void setUp() {
        onView(withId(R.id.editTextEmail)).perform(typeText("yuzhew@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextPassword)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        sleep();
        onView(withId(R.id.buttonContinue)).perform(click());
        sleep();
    }

    @Test
    public void bookingPageOnCreate_isSuccessful() throws UiObjectNotFoundException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("UAC Lap Swim"));
        marker.click();
        sleep();
        onView(withId(R.id.bookingPage)).check(matches(isDisplayed()));
    }

    private static void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
