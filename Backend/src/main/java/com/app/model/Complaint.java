package com.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "complaints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.ORDINAL)
    private ComplaintCategory category;
    
    @Enumerated(EnumType.ORDINAL)
    private ComplaintStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flat_id", nullable = false)
    private Flat flat;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime resolvedAt;
    
    @Column(columnDefinition = "TEXT")
    private String resolution;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private FlatMember createdBy;
    
//    @ManyToOne
//    @JoinColumn(name = "society_id", nullable = false)
//    private Society society;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = ComplaintStatus.PENDING;
    }

	
    
}

