package com.example.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;


public class EspressoProfileActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> MainActivityRule = new ActivityScenarioRule<>(
            MainActivity.class);

    @Test
    public void testSuccessLoginAndLoadPassword() {
        onView(withId(R.id.editTextEmail)).perform(typeText("yuzhew@usc.edu"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.editTextPassword)).perform(typeText("123456"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.buttonLogin)).perform(click());
        sleep();
        onView(withId(R.id.editTextUSCID)).check(matches(withText(containsString("8106395679"))));

    }

    private static void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
