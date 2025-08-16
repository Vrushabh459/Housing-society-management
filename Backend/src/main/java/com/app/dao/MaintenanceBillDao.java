package com.app.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.model.MaintenanceBill;

@Repository
public interface MaintenanceBillDao extends JpaRepository<MaintenanceBill, Long> {
    
    List<MaintenanceBill> findByFlatId(Long flatId);
    
    List<MaintenanceBill> findByFlatIdAndPaid(Long flatId, boolean paid);
    
    Optional<MaintenanceBill> findByBillNumber(String billNumber);
    
    List<MaintenanceBill> findByDueDateBefore(LocalDate date);
    
    List<MaintenanceBill> findByDueDateBeforeAndPaid(LocalDate date, boolean paid);
    
    @Query("SELECT mb FROM MaintenanceBill mb JOIN mb.flat f JOIN f.building b WHERE b.society.id = :societyId")
    List<MaintenanceBill> findBySocietyId(Long societyId);
    
    @Query("SELECT mb FROM MaintenanceBill mb JOIN mb.flat f JOIN f.building b WHERE b.society.id = :societyId AND mb.paid = :paid")
    List<MaintenanceBill> findBySocietyIdAndPaid(Long societyId, boolean paid);
}
