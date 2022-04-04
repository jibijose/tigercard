package com.sahaj.jibi.tigercard.repository;

import com.sahaj.jibi.tigercard.entity.PeakHourPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;

@Repository
public interface PeakHourPeriodRepository extends JpaRepository<PeakHourPeriod, Long> {

    @Query("select peakHourPeriod from PeakHourPeriod peakHourPeriod " +
            "where dayOfWeek = :dayOfWeek and timeStart <= :startTime and timeEnd >= :startTime")
    public PeakHourPeriod findByDayAndTime(DayOfWeek dayOfWeek, Integer startTime);

}
