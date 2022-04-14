package com.example.myapplication;


import com.google.firebase.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class SummaryPageActivityUtilUnitTest {

//    ArrayList<Object> apptArray = (ArrayList<Object>) doc.get("Appointments");
//
//
//    // fetch and store all the data fields inside multiple arrays
//    ArrayList<String> recCenterNames = new ArrayList<>(), dates = new ArrayList<>(),
//            times = new ArrayList<>(), prevOrCurrents = new ArrayList<>();
//    ArrayList<Timestamp> timestamps = new ArrayList<>();
//    ArrayList<Integer> indices = new ArrayList<>();

    ArrayList<Object> apptArray;
    ArrayList<String> recCenterNamesOutput, datesOutput, timesOutput, prevOrCurrentsOutput;
    ArrayList<Timestamp> timestampsOutput;
    ArrayList<Integer> indicesOutput;
    Date date1;
    @Before
    public void setUpArrays()
    {
        apptArray = new ArrayList<>();
        Map<String,Object> appointment1 = new HashMap<>();
        Map<String,Object> timeInt1 = new HashMap<>();
        timeInt1.put("capacity",new Long(1));
        timeInt1.put("currentRegistered",new Long(1));
        Calendar cal = Calendar.getInstance();
        cal.set(2022,9,1,9,0,0); //actually sets month to 10 for some reason
        date1 = cal.getTime();
        timeInt1.put("date", new Timestamp(date1));
        timeInt1.put("duration", new Long(1));
        timeInt1.put("recCenter","Lyon Center");
        timeInt1.put("slotId","00001");
        timeInt1.put("waitingList",new ArrayList<String>());

        appointment1.put("recCenterName","Lyon Center");
        appointment1.put("successfullyBooked",true);
        appointment1.put("timeInterval",timeInt1);
        appointment1.put("USCID","8106395679");
        appointment1.put("email","yuzhew@usc.edu");
        appointment1.put("photoFileName","");
        appointment1.put("userName","");

        apptArray.add(appointment1);


        recCenterNamesOutput = new ArrayList<>();
        datesOutput = new ArrayList<>();
        timesOutput = new ArrayList<>();
        prevOrCurrentsOutput = new ArrayList<>();
        timestampsOutput = new ArrayList<>();
        indicesOutput = new ArrayList<>();

    }
    @Test
    public void appointmentDataParse_isCorrect()
    {

        assertTrue("basic appointment data parsing should be valid",
                SummaryPageActivity.appointmentDataParse(apptArray, recCenterNamesOutput, datesOutput, timesOutput, prevOrCurrentsOutput, timestampsOutput, indicesOutput));

        ArrayList<String> recCenterNames = new ArrayList<>(), dates = new ArrayList<>(),
                times = new ArrayList<>(), prevOrCurrents = new ArrayList<>();
        ArrayList<Timestamp> timestamps = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        recCenterNames.add("Lyon Center");
        dates.add("Date: 2022-10-01");
        times.add("Time: 09:00:00 - 10:00:00");
        prevOrCurrents.add("current appointment");

        timestamps.add(new Timestamp(date1));
        indices.add(0);

        Assert.assertEquals("list of RecCenters should be the same as expected",recCenterNamesOutput, recCenterNames);
        Assert.assertEquals("list of dates should be the same as expected",datesOutput, dates);
        Assert.assertEquals("list of times should be the same as expected",timesOutput, times);
        Assert.assertEquals("list of prevOrCurrents should be the same as expected",prevOrCurrentsOutput, prevOrCurrents);
        Assert.assertEquals("list of timestamps should be the same as expected",timestampsOutput, timestamps);
        Assert.assertEquals("list of indices should be the same as expected",indicesOutput, indices);

    }


}
