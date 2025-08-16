package com.app.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "societies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Society {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String pincode;
    
    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;
    
    @Column(name = "number_of_buildings")
    private Integer numberOfBuildings;
    
    @OneToMany(mappedBy = "society")
    private List<Building> buildings;
    
    @OneToMany(mappedBy = "society")
    private List<User> admins;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        
        if (registrationNumber == null || registrationNumber.isEmpty()) {
            registrationNumber = generateRegistrationNumber();
        }
    }
    private String generateRegistrationNumber() {
        // Generate a unique registration number based on timestamp and name
        String timestamp = String.valueOf(System.currentTimeMillis());
        String namePrefix = name != null ? name.substring(0, Math.min(3, name.length())).toUpperCase() : "SOC";
        return namePrefix + timestamp.substring(timestamp.length() - 6);
    }
   
	
    
	
    
}