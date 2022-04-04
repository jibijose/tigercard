package com.sahaj.jibi.tigercard.service;

import com.sahaj.jibi.tigercard.entity.CappingLimit;
import com.sahaj.jibi.tigercard.entity.PeakHourPeriod;
import com.sahaj.jibi.tigercard.entity.ZoneFare;
import com.sahaj.jibi.tigercard.model.Trip;
import com.sahaj.jibi.tigercard.model.TripWithFare;
import com.sahaj.jibi.tigercard.repository.CappingLimitRepository;
import com.sahaj.jibi.tigercard.repository.PeakHourPeriodRepository;
import com.sahaj.jibi.tigercard.repository.ZoneFareRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;

@Service
@Slf4j
public class FareService {

    @Autowired
    private PeakHourPeriodRepository peakHourPeriodRepository;

    @Autowired
    private ZoneFareRepository zoneFareRepository;

    @Autowired
    private CappingLimitRepository cappingLimitRepository;

    public int calculateTotalFare(List<Trip> trips) {
        if (trips == null || trips.isEmpty()) {
            return 0;
        }

        List<TripWithFare> tripsWithFare = new ArrayList<>();
        for (Trip trip : trips) {
            TripWithFare tripWithFare = new TripWithFare(trip);
            tripsWithFare.add(tripWithFare);
        }

        //  Find and set peak/nonpeak fares per trip
        calculatePeakNonPeakTripFare(tripsWithFare);

        //  Find and set daily capping limits
        Map<DailyTripGroup, List<TripWithFare>> mapDailyTripGroup = groupDailyTrips(tripsWithFare);
        findDailyCappingLimits(mapDailyTripGroup);
        calculateDailyCappingLimits(mapDailyTripGroup);

        //  Find and set weekly capping limits
        Map<WeeklyTripGroup, List<TripWithFare>> mapWeeklyTripGroup = groupWeeklyTrips(tripsWithFare);
        findWeeklyCappingLimits(mapWeeklyTripGroup);
        calculateWeeklyCappingLimits(mapWeeklyTripGroup);

        //  Find total fare
        int finalTripCost = findTotalTripCost(tripsWithFare);
        return finalTripCost;
    }

    //  Set peak and non-peak fares for each trip
    private void calculatePeakNonPeakTripFare(List<TripWithFare> tripsWithFare) {
        for (TripWithFare tripWithFare : tripsWithFare) {
            boolean isPeakHourTravel = isPeakHourTravel(tripWithFare);

            ZoneFare zoneFare = null;
            try {
                zoneFare = zoneFareRepository.findByZones(tripWithFare.getStartZone(), tripWithFare.getEndZone());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }

            if (zoneFare == null) {
                throw new RuntimeException("Fares not mapped for zones " + tripWithFare.getStartZone() + " and " + tripWithFare.getEndZone());
            }

            if (isPeakHourTravel) {
                tripWithFare.setCalculatedFare(zoneFare.getChargePeakHour());
                tripWithFare.setExplanation("Peak hours Single fare");
            } else {
                tripWithFare.setCalculatedFare(zoneFare.getChargeNonPeakHour());
                tripWithFare.setExplanation("Off-peak single fare");
            }
        }

        int tripCost = findTotalTripCost(tripsWithFare);
        log.debug("Trip cost with peaks is {} for trips {}", tripCost, Arrays.toString(tripsWithFare.toArray()));
    }

    //  Group trips into day wise groups.
    private Map<DailyTripGroup, List<TripWithFare>> groupDailyTrips(List<TripWithFare> tripsWithFare) {
        Map<DailyTripGroup, List<TripWithFare>> mapDailyTripGroup = new HashMap<>();
        DayOfWeek currentDayOfWeek = null;
        int dailyGroupId = 0;
        List<TripWithFare> currentDailyGroupTripWithFare = null;
        DailyTripGroup currentDailyTripGroup;

        for (TripWithFare tripWithFare : tripsWithFare) {
            if (currentDayOfWeek == null || !currentDayOfWeek.equals(tripWithFare.getDayOfWeek())) {
                dailyGroupId++;
                currentDayOfWeek = tripWithFare.getDayOfWeek();
                currentDailyGroupTripWithFare = new ArrayList<>();

                currentDailyTripGroup = new DailyTripGroup();
                currentDailyTripGroup.setGroupId(dailyGroupId);
                currentDailyTripGroup.setDayOfWeek(currentDayOfWeek);
                mapDailyTripGroup.put(currentDailyTripGroup, currentDailyGroupTripWithFare);
            }
            currentDailyGroupTripWithFare.add(tripWithFare);
        }
        return mapDailyTripGroup;
    }

    //  Group trips into week wise groups.
    private Map<WeeklyTripGroup, List<TripWithFare>> groupWeeklyTrips(List<TripWithFare> tripsWithFare) {
        Map<WeeklyTripGroup, List<TripWithFare>> mapWeeklyTripGroup = new HashMap<>();
        DayOfWeek currentDayOfWeek = null;
        int weeklyGroupId = 0;
        List<TripWithFare> currentWeeklyGroupTripWithFare = null;
        WeeklyTripGroup currentWeeklyTripGroup;

        for (TripWithFare tripWithFare : tripsWithFare) {
            if (currentDayOfWeek == null
                    || (tripWithFare.getDayOfWeek() == DayOfWeek.MONDAY && !currentDayOfWeek.equals(DayOfWeek.MONDAY))) {
                weeklyGroupId++;
                currentWeeklyGroupTripWithFare = new ArrayList<>();

                currentWeeklyTripGroup = new WeeklyTripGroup();
                currentWeeklyTripGroup.setGroupId(weeklyGroupId);
                mapWeeklyTripGroup.put(currentWeeklyTripGroup, currentWeeklyGroupTripWithFare);
            }
            currentDayOfWeek = tripWithFare.getDayOfWeek();
            currentWeeklyGroupTripWithFare.add(tripWithFare);
        }
        return mapWeeklyTripGroup;
    }

    //  Find farthest trip in a day and fetch its daily capping limit.
    private void findDailyCappingLimits(Map<DailyTripGroup, List<TripWithFare>> mapDailyTripGroup) {
        for (DailyTripGroup dailyTripGroup : mapDailyTripGroup.keySet()) {
            List<TripWithFare> listDailyTripsWithFare = mapDailyTripGroup.get(dailyTripGroup);

            int startFarthestZone = 0;
            int endFarthestZone = 0;
            for (TripWithFare dailyTripWithFare : listDailyTripsWithFare) {
                if ((startFarthestZone == 0 && endFarthestZone == 0)
                        || (Math.abs(endFarthestZone - startFarthestZone) < Math.abs(dailyTripWithFare.getEndZone() - dailyTripWithFare.getStartZone()))) {
                    startFarthestZone = dailyTripWithFare.getStartZone();
                    endFarthestZone = dailyTripWithFare.getEndZone();
                }
            }

            CappingLimit cappingLimit = cappingLimitRepository.findByZones(startFarthestZone, endFarthestZone);
            log.debug("Farthest journey daily limit {} for dailyTripGroupId {} and day {} in zone {} - {} "
                    , cappingLimit.getDailyCap(), dailyTripGroup.getGroupId(), dailyTripGroup.getDayOfWeek(), startFarthestZone, endFarthestZone);
            dailyTripGroup.setDailyCappingLimit(cappingLimit.getDailyCap());
            dailyTripGroup.setFarthestStartZone(startFarthestZone);
            dailyTripGroup.setFarthestEndZone(endFarthestZone);
        }
    }

    //  Find farthest trip in a week and fetch its weekly capping limit.
    private void findWeeklyCappingLimits(Map<WeeklyTripGroup, List<TripWithFare>> mapWeeklyTripGroup) {
        for (WeeklyTripGroup weeklyTripGroup : mapWeeklyTripGroup.keySet()) {
            List<TripWithFare> listWeeklyTripsWithFare = mapWeeklyTripGroup.get(weeklyTripGroup);

            int startFarthestZone = 0;
            int endFarthestZone = 0;
            for (TripWithFare dailyTripWithFare : listWeeklyTripsWithFare) {
                if ((startFarthestZone == 0 && endFarthestZone == 0)
                        || (Math.abs(endFarthestZone - startFarthestZone) < Math.abs(dailyTripWithFare.getEndZone() - dailyTripWithFare.getStartZone()))) {
                    startFarthestZone = dailyTripWithFare.getStartZone();
                    endFarthestZone = dailyTripWithFare.getEndZone();
                }
            }

            CappingLimit cappingLimit = cappingLimitRepository.findByZones(startFarthestZone, endFarthestZone);
            log.debug("Farthest journey weekly limit {} for weeklyTripGroupId {} in zone {} - {} "
                    , cappingLimit.getWeeklyCap(), weeklyTripGroup.getGroupId(), startFarthestZone, endFarthestZone);
            weeklyTripGroup.setWeeklyCappingLimit(cappingLimit.getWeeklyCap());
        }
    }

    //  Set fares based on farthest daily capping limits.
    private void calculateDailyCappingLimits(Map<DailyTripGroup, List<TripWithFare>> mapDailyTripGroup) {
        for (DailyTripGroup dailyTripGroup : mapDailyTripGroup.keySet()) {
            List<TripWithFare> listDailyTripsWithFare = mapDailyTripGroup.get(dailyTripGroup);
            int currentTotalDailyFare = 0;

            for (TripWithFare dailyTripWithFare : listDailyTripsWithFare) {
                if (dailyTripGroup.getDailyCappingLimit() <= currentTotalDailyFare + dailyTripWithFare.getCalculatedFare()) {
                    int newTripFare = dailyTripGroup.getDailyCappingLimit() - currentTotalDailyFare;
                    dailyTripWithFare.setExplanation("The Daily cap reached " + dailyTripGroup.getDailyCappingLimit()
                            + " for zone " + dailyTripGroup.getFarthestStartZone() + " - " + dailyTripGroup.getFarthestEndZone()
                            + ". Charged " + newTripFare + " instead of " + dailyTripWithFare.getCalculatedFare());
                    dailyTripWithFare.setCalculatedFare(newTripFare);
                }
                currentTotalDailyFare = currentTotalDailyFare + dailyTripWithFare.getCalculatedFare();
            }

            int tripCost = findTotalTripCost(listDailyTripsWithFare);
            log.debug("Trip cost with daily limits is {} for trips {}", tripCost, Arrays.toString(listDailyTripsWithFare.toArray()));
        }
    }

    //  Set fares based on farthest weekly capping limits.
    private void calculateWeeklyCappingLimits(Map<WeeklyTripGroup, List<TripWithFare>> mapWeeklyTripGroup) {
        for (WeeklyTripGroup weeklyTripGroup : mapWeeklyTripGroup.keySet()) {
            List<TripWithFare> listWeeklyTripsWithFare = mapWeeklyTripGroup.get(weeklyTripGroup);
            int currentTotalWeeklyFare = 0;

            for (TripWithFare weeklyTripWithFare : listWeeklyTripsWithFare) {
                if (weeklyTripGroup.getWeeklyCappingLimit() <= currentTotalWeeklyFare + weeklyTripWithFare.getCalculatedFare()) {
                    int newTripFare = weeklyTripGroup.getWeeklyCappingLimit() - currentTotalWeeklyFare;
                    weeklyTripWithFare.setExplanation("A weekly cap of " + weeklyTripGroup.getWeeklyCappingLimit() + " reached");
                    weeklyTripWithFare.setCalculatedFare(newTripFare);
                }
                currentTotalWeeklyFare = currentTotalWeeklyFare + weeklyTripWithFare.getCalculatedFare();
            }
        }
    }

    private boolean isPeakHourTravel(Trip trip) {
        PeakHourPeriod peakHourPeriod = peakHourPeriodRepository.findByDayAndTime(trip.getDayOfWeek(), trip.getStartTime());
        if (peakHourPeriod == null) {
            return false;
        }
        return true;
    }

    private int findTotalTripCost(List<TripWithFare> tripsWithFare) {
        int totalTripCost = 0;
        for (TripWithFare tripWithFare : tripsWithFare) {
            totalTripCost = totalTripCost + tripWithFare.getCalculatedFare();
        }
        return totalTripCost;
    }

    @Getter
    @Setter
    static class DailyTripGroup {
        private Integer groupId;
        private DayOfWeek dayOfWeek;
        private int dailyCappingLimit;
        private int farthestStartZone;
        private int farthestEndZone;
    }

    @Getter
    @Setter
    static class WeeklyTripGroup {
        private Integer groupId;
        private int weeklyCappingLimit;
    }
}


