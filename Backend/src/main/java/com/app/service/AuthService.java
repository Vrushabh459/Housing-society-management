package com.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceAlreadyExistsException;
import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.UserMapper;
import com.app.dao.SocietyDao;
import com.app.dao.UserDao;
import com.app.dto.AuthRequest;
import com.app.dto.JwtResponseDTO;
import com.app.dto.LoginDTO;
import com.app.dto.UserDTO;
import com.app.model.Society;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDao userRepository;
    private final SocietyDao societyRepository;
    private final SocietyService societyService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO registerUser(AuthRequest request) {
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }

        // Check if phone already exists
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone number already in use");
        }

        Society society = null;

        // Handle society assignment based on role
        if (request.getRole() == UserRole.ADMIN && request.getSocietyCreationRequest() != null) {
            // Admin creating a new society
            society = societyService.createSociety(request.getSocietyCreationRequest());
        } else if (request.getRole() != UserRole.ADMIN && request.getSocietyId() != null) {
            // Resident or Guard selecting an existing society
            society = societyRepository.findById(request.getSocietyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + request.getSocietyId()));
        } else if (request.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Society ID is required for Resident and Guard roles");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .society(society)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public JwtResponseDTO login(LoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        String token = tokenProvider.generateToken(user);

        return JwtResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .societyId(user.getSociety() != null ? user.getSociety().getId() : null)
                .societyName(user.getSociety() != null ? user.getSociety().getName() : null)
                .build();
    }
}