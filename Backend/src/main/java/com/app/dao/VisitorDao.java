package com.app.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.model.Visitor;

@Repository
public interface VisitorDao extends JpaRepository<Visitor, Long> {
    
    List<Visitor> findByvisitingFlatId(Long flatId);
    
    List<Visitor> findByLoggedById(Long userId);
    
    List<Visitor> findByApproved(boolean approved);
    
    List<Visitor> findByExitTimeIsNull();
    
    List<Visitor> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT vl FROM Visitor vl JOIN vl.visitingFlat f JOIN f.building b WHERE b.society.id = :societyId")
    List<Visitor> findBySocietyId(Long societyId);
    
    @Query("SELECT vl FROM Visitor vl JOIN vl.visitingFlat f JOIN f.building b WHERE b.society.id = :societyId AND vl.approved = :approved")
    List<Visitor> findBySocietyIdAndApproved(Long societyId, boolean approved);
    
    @Query("SELECT vl FROM Visitor vl JOIN vl.visitingFlat f JOIN f.building b WHERE b.society.id = :societyId AND vl.exitTime IS NULL")
    List<Visitor> findBySocietyIdAndExitTimeIsNull(Long societyId);
}
