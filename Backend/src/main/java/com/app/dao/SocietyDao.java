package com.app.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.model.Society;

@Repository
public interface SocietyDao extends JpaRepository<Society, Long> {
    
    boolean existsByName(String name);
    
    Optional<Society> findByName(String name);
}
