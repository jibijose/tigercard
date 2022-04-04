package com.sahaj.jibi.tigercard.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Getter
@Setter
public class ZoneFare {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer zoneStart;
    private Integer zoneEnd;
    private Integer chargePeakHour;
    private Integer chargeNonPeakHour;

    public ZoneFare() {

    }

    public ZoneFare(Integer zoneStart, Integer zoneEnd, Integer chargePeakHour, Integer chargeNonPeakHour) {
        this.zoneStart = zoneStart;
        this.zoneEnd = zoneEnd;
        this.chargePeakHour = chargePeakHour;
        this.chargeNonPeakHour = chargeNonPeakHour;
    }
}
