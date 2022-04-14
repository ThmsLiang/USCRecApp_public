package com.example.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

public class RecCenterJUnitTest {

    @Test
    public void testHardcodedLyonDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.lyon.getTimeSlots().get(0).getDate();
        assertEquals(test, goal);
    }

    @Test
    public void testHardcodedCromwellDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.Cromwell_Track.getTimeSlots().get(0).getDate();
        assertEquals(test, goal);
    }

    @Test
    public void testHardcodedUACDate_ReturnsTrue(){
        Date goal = RecCenter.getCalendar();
        Date test = RecCenter.uac.getTimeSlots().get(0).getDate();
        assertEquals(test, goal);
    }
}
