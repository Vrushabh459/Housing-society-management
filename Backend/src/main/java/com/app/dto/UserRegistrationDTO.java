package com.app.dto;

import com.app.model.UserRole;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    @NotBlank(message = "Name is required")
    private String name;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    @NotNull(message = "Role is required")
    private UserRole role;
    
    private SocietyDTO society;
}

