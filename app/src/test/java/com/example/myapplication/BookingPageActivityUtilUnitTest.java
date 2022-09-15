package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

public class BookingPageActivityUtilUnitTest {

    @Before
    public void setUp() {
    }

    @Test
    public void addHoursToJavaUtilDate_isCorrect() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,10);
        Date date = cal.getTime();
        int hour = 2;
        Date result = BookingPageActivity.addHoursToJavaUtilDate(date, hour);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        Date expected = cal.getTime();
        assertEquals(expected, result);
    }
}
