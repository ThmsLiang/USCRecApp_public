package com.example.myapplication;

import java.io.Serializable;

public class Appointment implements Serializable {
    String recCenterName;
    TimeSlot timeInterval;
    boolean successfullyBooked;

    // getters and setters
    public String getRecCenterName() {
        return recCenterName;
    }

    public void setRecCenterName(String recCenterName) {
        this.recCenterName = recCenterName;
    }

    public TimeSlot getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeSlot timeInterval) {
        this.timeInterval = timeInterval;
    }

    public boolean isSuccessfullyBooked() {
        return successfullyBooked;
    }

    public void setSuccessfullyBooked(boolean successfullyBooked) {
        this.successfullyBooked = successfullyBooked;
    }

    // other methods
    public String toString(){
        java.util.Date date = timeInterval.getDate();
        return recCenterName;
    }
}
