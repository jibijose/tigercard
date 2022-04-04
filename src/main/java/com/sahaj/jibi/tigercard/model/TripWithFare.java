package com.sahaj.jibi.tigercard.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class TripWithFare extends Trip {

    private int calculatedFare;
    private String explanation;

    public TripWithFare(Trip trip) {
        super(trip.getDayOfWeek(), trip.getStartTime(), trip.getStartZone(), trip.getEndZone());
    }

    @Override
    public String toString() {
        return "TripWithFare{" +
                "Trip=" + super.toString() +
                ", calculatedFare=" + calculatedFare +
                ", explanation='" + explanation + '\'' +
                '}';
    }
}
