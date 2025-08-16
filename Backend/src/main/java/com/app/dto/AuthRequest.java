package com.app.dto;

import com.app.model.UserRole;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

	@Data
	@Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class AuthRequest {
        
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
        private String phone;
        
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        
        @NotBlank(message = "Confirm password is required")
        private String confirmPassword;
        
        private UserRole role;
        
        private Long societyId;
        
        // Only for ADMIN role when creating a new society
        private SocietyCreationRequest societyCreationRequest;
}
