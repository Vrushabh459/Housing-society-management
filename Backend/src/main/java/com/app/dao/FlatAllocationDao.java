package com.app.dao;

import com.app.model.AllocationStatus;
import com.app.model.FlatAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlatAllocationDao extends JpaRepository<FlatAllocation, Long> {

    List<FlatAllocation> findByUserId(Long userId);

    @Query("SELECT fa FROM FlatAllocation fa JOIN fa.flat f JOIN f.building b WHERE b.society.id = :societyId")
    List<FlatAllocation> findBySocietyId(Long societyId);

    @Query("SELECT fa FROM FlatAllocation fa JOIN fa.flat f JOIN f.building b WHERE b.society.id = :societyId AND fa.status = :status")
    List<FlatAllocation> findBySocietyIdAndStatus(Long societyId, AllocationStatus status);
}
