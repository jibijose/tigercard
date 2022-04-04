package com.sahaj.jibi.tigercard.repository;

import com.sahaj.jibi.tigercard.entity.CappingLimit;
import com.sahaj.jibi.tigercard.entity.ZoneFare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CappingLimitRepository extends JpaRepository<CappingLimit, Long> {

    @Query("select cappingLimit from CappingLimit cappingLimit where zoneStart = :zoneStart and zoneEnd = :zoneEnd")
    public CappingLimit findByZones(Integer zoneStart, Integer zoneEnd);

}
