package com.scenery7f.timeaxis.model;

import java.util.Calendar;

/**
 * 记录时间段
 * Created by snoopy on 2017/9/15.
 */

public class PeriodTime {

    private Calendar startTime;

    private Calendar stopTime;

    public PeriodTime(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getStopTime() {
        return stopTime;
    }

    public void setStopTime(Calendar stopTime) {
        this.stopTime = stopTime;
    }
}
