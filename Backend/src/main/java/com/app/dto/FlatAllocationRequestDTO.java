package com.app.dto;

import com.app.model.AllocationStatus;
import com.app.model.ResidentType;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class FlatAllocationRequestDTO {
    private Long id;
    
    @NotNull(message = "Flat ID is required")
    private Long flatId;
    
    private String flatNumber;
    
    @Min(value = 1, message = "Family members must be at least 1")
    private Integer familyMembers;
    
    @NotBlank(message = "Occupation is required")
    private String occupation;
    
//    @Pattern(regexp = "^[0-9]{10}$", message = "Emergency contact must be 10 digits")
//    private String emergencyContact;
    
    private AllocationStatus status;
    private ResidentType residentType;
    private String userName;
    private String userEmail;
}
