package com.sahaj.jibi.tigercard.service;

import com.sahaj.jibi.tigercard.entity.CappingLimit;
import com.sahaj.jibi.tigercard.entity.PeakHourPeriod;
import com.sahaj.jibi.tigercard.entity.ZoneFare;
import com.sahaj.jibi.tigercard.model.Trip;
import com.sahaj.jibi.tigercard.repository.CappingLimitRepository;
import com.sahaj.jibi.tigercard.repository.PeakHourPeriodRepository;
import com.sahaj.jibi.tigercard.repository.ZoneFareRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class FareServiceTest {

    @Autowired
    private FareService fareService;

    @Autowired
    private PeakHourPeriodRepository peakHourPeriodRepository;

    @Autowired
    private ZoneFareRepository zoneFareRepository;

    @Autowired
    private CappingLimitRepository cappingLimitRepository;

    @BeforeEach
    void beforeEach() {
        setupDatabaseData();
    }

    @AfterEach
    void afterEach() {
        removeDatabaseData();
    }

    @Test
    public void findFare_BoundaryConditions() {
        assertEquals(0, fareService.calculateTotalFare(null), "Fare is zero for null trips");

        List<Trip> trips = new ArrayList<>();
        assertEquals(0, fareService.calculateTotalFare(trips), "Fare is zero for no trips");
    }

    @Test
    public void findFare_WithoutCapping_NonExistent() {
        List<Trip> trips = new ArrayList<>();
        trips.add(new Trip(DayOfWeek.MONDAY, 600, 1, 100));

        RuntimeException runtimeException = assertThrows(
                RuntimeException.class,
                () -> fareService.calculateTotalFare(trips), "Expected calculateTotalFare() to throw, but it didn't"
        );

        assertTrue(runtimeException.getMessage().contains("Fares not mapped for zones"));
    }

    @Test
    public void findFare_WithoutCapping() {
        List<Trip> trips = null;
        int fare;

        trips = setup1TripNoCappingCondition();
        fare = fareService.calculateTotalFare(trips);
        assertEquals(35, fare, "Fare is per trip peak hours");

        trips = setup2TripNoCappingCondition();
        fare = fareService.calculateTotalFare(trips);
        assertEquals(60, fare, "Fare is per trip peak hours");
    }

    @Test
    public void findFare_WithDailyCapping() {
        List<Trip> trips = null;
        int fare;

        trips = setupDailyTripCappingCondition();
        fare = fareService.calculateTotalFare(trips);
        assertEquals(120, fare, "Fare is based on daily capping");
    }

    @Test
    public void findFare_WithWeeklyCapping() {
        List<Trip> trips = null;
        int fare;

        trips = setupWeeklyTripCappingCondition();
        fare = fareService.calculateTotalFare(trips);
        assertEquals(600, fare, "Fare is based on weekly capping");

        trips.add(new Trip(DayOfWeek.MONDAY, 360, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 370, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 380, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 390, 1, 1));
        fare = fareService.calculateTotalFare(trips);
        assertEquals(700, fare, "Fare is based on weekly capping");
    }

    private List<Trip> setup1TripNoCappingCondition() {
        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip(DayOfWeek.MONDAY, 620, 2, 1));

        return trips;
    }

    private List<Trip> setup2TripNoCappingCondition() {
        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip(DayOfWeek.MONDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 645, 1, 1));

        return trips;
    }

    private List<Trip> setupDailyTripCappingCondition() {
        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip(DayOfWeek.MONDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 645, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 975, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 1095, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 1140, 1, 2));

        return trips;
    }

    private List<Trip> setupWeeklyTripCappingCondition() {
        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip(DayOfWeek.MONDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 645, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 975, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 1095, 1, 1));
        trips.add(new Trip(DayOfWeek.MONDAY, 1140, 1, 2));

        trips.add(new Trip(DayOfWeek.TUESDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.TUESDAY, 645, 1, 1));
        trips.add(new Trip(DayOfWeek.TUESDAY, 975, 1, 1));
        trips.add(new Trip(DayOfWeek.TUESDAY, 1095, 1, 1));
        trips.add(new Trip(DayOfWeek.TUESDAY, 1140, 1, 2));

        trips.add(new Trip(DayOfWeek.WEDNESDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.WEDNESDAY, 645, 1, 1));
        trips.add(new Trip(DayOfWeek.WEDNESDAY, 975, 1, 1));
        trips.add(new Trip(DayOfWeek.WEDNESDAY, 1095, 1, 1));
        trips.add(new Trip(DayOfWeek.WEDNESDAY, 1140, 1, 2));

        trips.add(new Trip(DayOfWeek.THURSDAY, 620, 2, 1));
        trips.add(new Trip(DayOfWeek.THURSDAY, 645, 1, 1));
        trips.add(new Trip(DayOfWeek.THURSDAY, 975, 1, 1));
        trips.add(new Trip(DayOfWeek.THURSDAY, 1095, 1, 1));
        trips.add(new Trip(DayOfWeek.THURSDAY, 1140, 1, 2));

        trips.add(new Trip(DayOfWeek.FRIDAY, 360, 2, 2));
        trips.add(new Trip(DayOfWeek.FRIDAY, 370, 2, 2));
        trips.add(new Trip(DayOfWeek.FRIDAY, 380, 2, 2));
        trips.add(new Trip(DayOfWeek.FRIDAY, 390, 2, 2));

        trips.add(new Trip(DayOfWeek.SATURDAY, 360, 2, 2));
        trips.add(new Trip(DayOfWeek.SATURDAY, 370, 2, 2));
        trips.add(new Trip(DayOfWeek.SATURDAY, 380, 2, 2));
        trips.add(new Trip(DayOfWeek.SATURDAY, 390, 2, 2));

        trips.add(new Trip(DayOfWeek.SUNDAY, 360, 2, 2));
        trips.add(new Trip(DayOfWeek.SUNDAY, 370, 2, 2));
        trips.add(new Trip(DayOfWeek.SUNDAY, 380, 2, 2));
        trips.add(new Trip(DayOfWeek.SUNDAY, 390, 2, 2));

        return trips;
    }

    private void setupDatabaseData() {
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.MONDAY, 420, 630));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.MONDAY, 1020, 1200));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.TUESDAY, 420, 630));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.TUESDAY, 1020, 1200));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.WEDNESDAY, 420, 630));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.WEDNESDAY, 1020, 1200));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.THURSDAY, 420, 630));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.THURSDAY, 1020, 1200));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.FRIDAY, 420, 630));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.FRIDAY, 1020, 1200));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.SATURDAY, 540, 660));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.SATURDAY, 1080, 1320));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.SUNDAY, 540, 660));
        peakHourPeriodRepository.save(new PeakHourPeriod(DayOfWeek.SUNDAY, 1080, 1320));

        zoneFareRepository.save(new ZoneFare(1, 1, 30, 25));
        zoneFareRepository.save(new ZoneFare(1, 2, 35, 30));
        zoneFareRepository.save(new ZoneFare(2, 1, 35, 30));
        zoneFareRepository.save(new ZoneFare(2, 2, 25, 20));

        cappingLimitRepository.save(new CappingLimit(1, 1, 100, 500));
        cappingLimitRepository.save(new CappingLimit(1, 2, 120, 600));
        cappingLimitRepository.save(new CappingLimit(2, 1, 120, 600));
        cappingLimitRepository.save(new CappingLimit(2, 2, 80, 400));
    }

    private void removeDatabaseData() {
        peakHourPeriodRepository.deleteAll();
        zoneFareRepository.deleteAll();
        cappingLimitRepository.deleteAll();
    }
}
