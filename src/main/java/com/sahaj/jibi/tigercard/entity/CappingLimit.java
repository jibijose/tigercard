package com.sahaj.jibi.tigercard.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@Getter
@Setter
public class CappingLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer zoneStart;
    private Integer zoneEnd;
    private Integer dailyCap;
    private Integer weeklyCap;

    public CappingLimit() {

    }

    public CappingLimit(Integer zoneStart, Integer zoneEnd, Integer dailyCap, Integer weeklyCap) {
        this.zoneStart = zoneStart;
        this.zoneEnd = zoneEnd;
        this.dailyCap = dailyCap;
        this.weeklyCap = weeklyCap;
    }
}
