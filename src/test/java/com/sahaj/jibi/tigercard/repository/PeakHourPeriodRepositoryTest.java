package com.sahaj.jibi.tigercard.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sahaj.jibi.tigercard.entity.PeakHourPeriod;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PeakHourPeriodRepositoryTest {

    @Autowired
    private PeakHourPeriodRepository peakHourPeriodRepository;

    @Test
    public void given_saveData_whenFetched_thenOK() {
        PeakHourPeriod peakHourPeriod = new PeakHourPeriod();
        peakHourPeriod.setDayOfWeek(DayOfWeek.MONDAY);
        peakHourPeriod.setTimeStart(600);
        peakHourPeriod.setTimeEnd(840);
        PeakHourPeriod peakHourPeriodSaved = peakHourPeriodRepository.save(peakHourPeriod);

        PeakHourPeriod peakHourPeriodFetched = peakHourPeriodRepository.getById(peakHourPeriodSaved.getId());

        assertEquals(peakHourPeriodFetched, peakHourPeriodSaved);
    }
}
