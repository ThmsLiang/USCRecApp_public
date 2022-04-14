package com.example.myapplication;

import android.content.Context;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityUtilUnitTest {

    EditText emailEdit, passwordEdit;

    @Before
    public void setUpEditTexts()
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        emailEdit = new EditText(context);
        passwordEdit = new EditText(context);
    }
    @Test
    public void isEmailValid_isCorrect()
    {

        assertTrue("basic USC email should be valid",MainActivity.isEmailValid("elimorri@usc.edu",emailEdit));
        assertFalse("empty email should be invalid",MainActivity.isEmailValid("",emailEdit));
        assertFalse("null email should be invalid",MainActivity.isEmailValid(null,emailEdit));
        assertFalse("non-USC email should be invalid", MainActivity.isEmailValid("eli.h.morris@gmail.com",emailEdit));
        assertFalse("email without user should be invalid", MainActivity.isEmailValid("@usc.edu",emailEdit));
        assertFalse("invalid USC email should be invalid", MainActivity.isEmailValid("elimorri@usc.edu.com",emailEdit));

    }

    @Test
    public void isPasswordValid_isCorrect()
    {
        assertTrue("a password with at least 6 characters should be valid",MainActivity.isPasswordValid("aEFfo#@$(8u '?\n@%#ih",passwordEdit));
        assertFalse("a null password should be invalid", MainActivity.isPasswordValid(null,passwordEdit));
        assertFalse("a password with less than 6 char should be invalid", MainActivity.isPasswordValid("# E12",passwordEdit));
    }
}
