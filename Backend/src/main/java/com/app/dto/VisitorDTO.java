package com.app.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorDTO {
    private Long id;
    private String name;
    private String phone;
    private String purpose;
    private Long flatId;
    private String flatNumber;
    private Long loggedById;
    private String loggedByName;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean approved;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime createdAt;
  
}
