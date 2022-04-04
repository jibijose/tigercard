package com.sahaj.jibi.tigercard.repository;

import com.sahaj.jibi.tigercard.entity.ZoneFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneFareRepository extends JpaRepository<ZoneFare, Long> {

    @Query("select zoneFare from ZoneFare zoneFare where zoneStart = :zoneStart and zoneEnd = :zoneEnd")
    public ZoneFare findByZones(Integer zoneStart, Integer zoneEnd);

}
