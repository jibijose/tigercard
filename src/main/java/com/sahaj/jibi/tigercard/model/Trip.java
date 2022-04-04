package com.sahaj.jibi.tigercard.model;

import lombok.*;

import java.time.DayOfWeek;

@Getter
@Setter
public class Trip {

    private DayOfWeek dayOfWeek;
    private int startTime;
    private int startZone;
    private int endZone;

    public Trip(DayOfWeek dayOfWeek, int startTime, int startZone, int endZone) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.startZone = startZone;
        this.endZone = endZone;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", startZone=" + startZone +
                ", endZone=" + endZone +
                '}';
    }
}
