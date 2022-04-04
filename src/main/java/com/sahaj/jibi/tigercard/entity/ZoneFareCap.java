package com.sahaj.jibi.tigercard.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ZoneFareCap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer zoneStart;
    private Integer zoneEnd;
    private Integer dailyCap;
    private Integer weeklyCap;
}
