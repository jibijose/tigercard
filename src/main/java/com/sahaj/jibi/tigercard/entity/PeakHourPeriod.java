package com.sahaj.jibi.tigercard.entity;

import lombok.*;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Data
@Getter
@Setter
public class PeakHourPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private DayOfWeek dayOfWeek;
    private Integer timeStart;
    private Integer timeEnd;

    public PeakHourPeriod() {

    }

    public PeakHourPeriod(DayOfWeek dayOfWeek, Integer timeStart, Integer timeEnd) {
        this.dayOfWeek = dayOfWeek;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }
}
