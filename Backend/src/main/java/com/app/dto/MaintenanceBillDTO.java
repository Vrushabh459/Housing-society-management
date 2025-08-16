package com.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceBillDTO {
    private Long id;
    private String billNumber;
    private LocalDate billDate;
    private LocalDate dueDate;
    private BigDecimal amount;
    private boolean paid;
    private LocalDate paymentDate;
    private String paymentReference;
    private Long flatId;
    private String flatNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
}