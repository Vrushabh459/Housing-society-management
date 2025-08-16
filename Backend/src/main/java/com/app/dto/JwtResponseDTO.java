package com.app.dto;

import com.app.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtResponseDTO {
	private String token;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String name;
    private String email;
    private UserRole role;
    private Long societyId;
    private String societyName;
}
