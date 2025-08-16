package com.app.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import com.app.model.User;
import com.app.model.UserRole;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhone(String phone);
    
    List<User> findBySocietyId(Long societyId);
    
    List<User> findBySocietyIdAndRole(Long societyId, UserRole role);
}
