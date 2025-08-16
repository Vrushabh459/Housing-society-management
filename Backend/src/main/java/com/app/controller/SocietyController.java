package com.app.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.SocietyCreationRequest;
import com.app.dto.SocietyDTO;
import com.app.model.Society;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.SocietyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/societies")
@RequiredArgsConstructor
public class SocietyController {

    private final SocietyService societyService;

    @GetMapping("/list")
    public ResponseEntity<List<SocietyDTO>> getAllSocieties() {
        List<SocietyDTO> societies = societyService.getAllSocieties();
        return ResponseEntity.ok(societies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocietyDTO> getSocietyById(@PathVariable Long id) {
        SocietyDTO society = societyService.getSocietyById(id);
        return ResponseEntity.ok(society);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Society> createSociety(@Valid @RequestBody SocietyCreationRequest societyrequest) {
    	Society createdSociety = societyService.createSociety(societyrequest);
        return new ResponseEntity<>(createdSociety, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SocietyDTO> updateSociety(
            @PathVariable Long id,
            @Valid @RequestBody SocietyDTO societyDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only update their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(id) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        SocietyDTO updatedSociety = societyService.updateSociety(id, societyDto);
        return ResponseEntity.ok(updatedSociety);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSociety(@PathVariable Long id) {
        societyService.deleteSociety(id);
        return ResponseEntity.noContent().build();
    }
}
