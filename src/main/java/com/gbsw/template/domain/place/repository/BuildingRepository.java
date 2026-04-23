package com.gbsw.template.domain.place.repository;

import com.gbsw.template.domain.place.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Query("SELECT DISTINCT b FROM Building b LEFT JOIN FETCH b.floors f LEFT JOIN FETCH f.rooms ORDER BY b.id ASC")
    List<Building> findAllWithFloorsAndRooms();
}
