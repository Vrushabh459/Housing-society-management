package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Building;

@Repository
public interface BuildingDao extends JpaRepository<Building, Long> {
    
    List<Building> findBySocietyId(Long societyId);
    
    Optional<Building> findByNameAndSocietyId(String name, Long societyId);
    
    boolean existsByNameAndSocietyId(String name, Long societyId);
}
