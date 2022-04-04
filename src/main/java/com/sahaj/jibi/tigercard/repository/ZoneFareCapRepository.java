package com.sahaj.jibi.tigercard.repository;

import com.sahaj.jibi.tigercard.entity.ZoneFareCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneFareCapRepository extends JpaRepository<ZoneFareCap, Long> {
}
