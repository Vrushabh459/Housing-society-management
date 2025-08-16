package com.app.dto;

import java.time.LocalDateTime;

import com.app.model.ComplaintCategory;
import com.app.model.ComplaintStatus;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    private Long id;
    private String title;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private Long flatId;
    private String flatNumber;
    private Long raisedById;
    private String raisedByName;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String resolution;
}
