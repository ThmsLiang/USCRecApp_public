package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

public class TimeSlotUnitTest {
    TimeSlot test;
    String recCenter;
    Date date;
    Integer capacity;
    User user;

    @Before
    public void setUp() {
        recCenter = "Lyon Center";
        date = new Date();
        capacity = 1000;
        test = new TimeSlot(recCenter, date, capacity);
        user = new User();
        user.setEmail("abcdefj@gmail.com");
    }

    @Test
    public void timeSlotInitialization_isCorrect() {
        assertEquals(test.getDate(), date);
        assertEquals(test.getCapacity(), capacity);
        assertEquals(test.getCurrentRegistered(), 0);
        Integer x =  1;
        assertEquals(test.duration, x);
    }

    @Test
    public void addToWaitingList_isCorrect() {
        assertTrue(test.getWaitingList().isEmpty());
        test.addToWaitingList(user);
        assertFalse(test.getWaitingList().isEmpty());
        assertEquals(test.getWaitingList().size(), 1);
        String email = test.getWaitingList().get(0);
        assertEquals(email, "abcdefj@gmail.com");
    }

    @Test
    public void addToWaitingListNullEmail_isCorrect() {
        assertTrue(test.getWaitingList().isEmpty());
        User userWithNullEmail = new User();
        test.addToWaitingList(userWithNullEmail);
        assertTrue(test.getWaitingList().isEmpty());
    }

    @Test
    public void addToWaitingListEmptyEmail_isCorrect() {
        assertTrue(test.getWaitingList().isEmpty());
        User userWithoutEmail = new User();
        userWithoutEmail.email = "";
        test.addToWaitingList(userWithoutEmail);
        assertTrue(test.getWaitingList().isEmpty());
    }
}