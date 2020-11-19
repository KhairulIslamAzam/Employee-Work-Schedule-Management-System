package com.example.entity;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class Schedule {
    private String date;
    private String startTime;
    private String endTime;
    private String shiftName;

    public Schedule() {
    }

    public Schedule(String date, String startTime, String endTime,String shiftName) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftName = shiftName;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
