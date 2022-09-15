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
public class ProfileActivityUtilUnitTest {

    EditText idEdit;

    @Before
    public void setUpEditTexts()
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        idEdit = new EditText(context);

    }
    @Test
    public void isUSCIDValid_isCorrect()
    {

        assertTrue("basic USC ID should be valid",ProfileActivity.isUSCIDValid("1234567890",idEdit));
        assertFalse("USC ID with < 10 numbers should be invalid",ProfileActivity.isUSCIDValid("123456789",idEdit));
        assertFalse("USC ID with > 10 numbers should be invalid",ProfileActivity.isUSCIDValid("12345678900",idEdit));
        assertFalse("null USC ID should be invalid",ProfileActivity.isUSCIDValid(null,idEdit));
        assertFalse("USC ID with non-numbers should be invalid",ProfileActivity.isUSCIDValid("123456789a",idEdit));
        assertFalse("USC ID with white space should be invalid",ProfileActivity.isUSCIDValid("1234\n56789",idEdit));

    }


}
