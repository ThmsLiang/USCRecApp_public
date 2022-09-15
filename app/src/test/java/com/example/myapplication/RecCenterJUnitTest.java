package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;
import java.text.*;

import java.util.Date;

public class RecCenterJUnitTest {

    public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testHardcodedLyonDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.lyon.getTimeSlots().get(0).getDate();
        String goal_str = dateFormat.format(goal);
        String test_str = dateFormat.format(test);
        assertEquals(goal_str, test_str);
    }

    @Test
    public void testHardcodedCromwellDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.Cromwell_Track.getTimeSlots().get(0).getDate();
        String goal_str = dateFormat.format(goal);
        String test_str = dateFormat.format(test);
        assertEquals(goal_str, test_str);
    }

    @Test
    public void testHardcodedUACDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.uac.getTimeSlots().get(0).getDate();
        String goal_str = dateFormat.format(goal);
        String test_str = dateFormat.format(test);
        assertEquals(goal_str, test_str);
    }
}
