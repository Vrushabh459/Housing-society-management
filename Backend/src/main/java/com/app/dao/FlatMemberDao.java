package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.model.FlatMember;

@Repository
public interface FlatMemberDao extends JpaRepository<FlatMember, Long> {
    
    List<FlatMember> findByFlatId(Long flatId);
    
    List<FlatMember> findByUserId(Long userId);
    
    Optional<FlatMember> findByUserIdAndFlatId(Long userId, Long flatId);
    
    List<FlatMember> findByFlatIdAndIsOwner(Long flatId, boolean isOwner);
    
    List<FlatMember> findByApproved(boolean approved);
    
    @Query("SELECT fm FROM FlatMember fm JOIN fm.flat f JOIN f.building b WHERE b.society.id = :societyId AND fm.approved = :approved")
    List<FlatMember> findBySocietyIdAndApproved(Long societyId, boolean approved);
}
