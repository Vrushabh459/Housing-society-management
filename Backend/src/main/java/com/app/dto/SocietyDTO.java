package com.app.dto;





import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SocietyDTO {
    private Long id;
    
    @NotBlank(message = "Society name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Pincode is required")
    private String pincode;
    
    private String registrationNumber;
    
    @Min(value = 1, message = "Number of buildings must be at least 1")
    private Integer numberOfBuildings;
}
    
	
    
    

