package com.sahaj.jibi.tigercard.repository;

import com.sahaj.jibi.tigercard.entity.ZoneFare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ZoneFareRepositoryTest {

    @Autowired
    private ZoneFareRepository zoneFareRepository;

    ZoneFare zoneFareSaved;

    @BeforeEach
    public void beforeAll() {
        ZoneFare zoneFare = new ZoneFare();
        zoneFare.setZoneStart(1);
        zoneFare.setZoneEnd(1);
        zoneFare.setChargePeakHour(30);
        zoneFare.setChargeNonPeakHour(25);
        zoneFareSaved = zoneFareRepository.save(zoneFare);
    }

    @Test
    public void given_saveData_whenFetched_thenOK() {
        ZoneFare zoneFareFetched = zoneFareRepository.getById(zoneFareSaved.getId());

        assertEquals(zoneFareSaved, zoneFareFetched);
    }

    @Test
    public void given_saveNoData_whenFetchedByZones_thenOK() {
        ZoneFare zoneFareFetched = zoneFareRepository.findByZones(1, 100);

        assertNull(zoneFareFetched);
    }

    @Test
    public void given_saveData_whenFetchedByZones_thenOK() {
        ZoneFare zoneFareFetched = zoneFareRepository.findByZones(1, 1);

        assertEquals(zoneFareSaved, zoneFareFetched);
    }
}
