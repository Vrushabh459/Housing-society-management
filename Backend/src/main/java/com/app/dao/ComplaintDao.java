package com.app.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.model.Complaint;
import com.app.model.ComplaintStatus;

@Repository
public interface ComplaintDao extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByFlatId(Long flatId);
    
    List<Complaint> findByCreatedById(Long userId);
    
    List<Complaint> findByStatus(ComplaintStatus status);
    
    @Query("SELECT c FROM Complaint c JOIN c.flat f JOIN f.building b WHERE b.society.id = :societyId")
    List<Complaint> findBySocietyId(Long societyId);
    
    @Query("SELECT c FROM Complaint c JOIN c.flat f JOIN f.building b WHERE b.society.id = :societyId AND c.status = :status")
    List<Complaint> findBySocietyIdAndStatus(Long societyId, ComplaintStatus status);
}
