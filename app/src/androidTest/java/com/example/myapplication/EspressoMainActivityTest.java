package com.example.myapplication;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;


public class EspressoMainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> MainActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);

    @Test
    public void testFailLoginPassword() {
        onView(withId(R.id.editTextEmail)).perform(typeText("yuzhew@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextPassword)).perform(typeText("1234567"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.id.buttonContinue)).check(doesNotExist());

    }

    @Test
    public void testFailLoginEmail() {
        onView(withId(R.id.editTextEmail)).perform(typeText("nonexistent@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextPassword)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        onView(withText(R.id.buttonContinue)).check(doesNotExist());

    }


}
