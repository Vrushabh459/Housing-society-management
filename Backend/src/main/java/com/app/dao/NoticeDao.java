package com.app.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Notice;

@Repository
public interface NoticeDao extends JpaRepository<Notice, Long> {
    
    List<Notice> findBySocietyId(Long societyId);
    
    List<Notice> findBySocietyIdAndIsActive(Long societyId, boolean active);
    
    List<Notice> findByCreatedById(Long userId);
    
    List<Notice> findByExpiresAtBeforeAndIsActive(LocalDateTime dateTime, boolean active);
}
