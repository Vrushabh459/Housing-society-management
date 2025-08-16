package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.model.Flat;

@Repository
public interface FlatDao extends JpaRepository<Flat, Long> {
    
    List<Flat> findByBuildingId(Long buildingId);
    
    Optional<Flat> findByFlatNumberAndBuildingId(String flatNumber, Long buildingId);
    
    boolean existsByFlatNumberAndBuildingId(String flatNumber, Long buildingId);
    
    @Query("SELECT f FROM Flat f JOIN f.building b WHERE b.society.id = :societyId")
    List<Flat> findBySocietyId(Long societyId);
}
