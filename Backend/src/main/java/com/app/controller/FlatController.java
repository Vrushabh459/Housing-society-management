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

import com.app.dto.FlatDTO;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.FlatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flats")
@RequiredArgsConstructor
public class FlatController {

    private final FlatService flatService;

    @GetMapping
    public ResponseEntity<List<FlatDTO>> getAllFlats(@AuthenticationPrincipal User currentUser) {
        // If user is an admin, resident, or guard, only return flats from their society
        if (currentUser.getSociety() != null) {
            List<FlatDTO> flats = flatService.getFlatsBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(flats);
        } else {
            // Super admin can see all flats
            List<FlatDTO> flats = flatService.getAllFlats();
            return ResponseEntity.ok(flats);
        }
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<FlatDTO>> getFlatsByBuildingId(
            @PathVariable Long buildingId,
            @AuthenticationPrincipal User currentUser) {
        
        List<FlatDTO> flats = flatService.getFlatsByBuildingId(buildingId);
        
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/society/{societyId}")
    public ResponseEntity<List<FlatDTO>> getFlatsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access flats from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<FlatDTO> flats = flatService.getFlatsBySocietyId(societyId);
        return ResponseEntity.ok(flats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlatDTO> getFlatById(@PathVariable Long id) {
        FlatDTO flat = flatService.getFlatById(id);
        return ResponseEntity.ok(flat);
    }
    
    // --- THIS IS THE NEW ENDPOINT ---
    @GetMapping("/my-flat")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<FlatDTO> getMyAllocatedFlat(@AuthenticationPrincipal User currentUser) {
        FlatDTO flatDto = flatService.getFlatByUserId(currentUser.getId());
        return ResponseEntity.ok(flatDto);
    }
    // --- END OF NEW ENDPOINT ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlatDTO> createFlat(
            @Valid @RequestBody FlatDTO flatDto,
            @AuthenticationPrincipal User currentUser) {
        
        FlatDTO createdFlat = flatService.createFlat(flatDto);
        return new ResponseEntity<>(createdFlat, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlatDTO> updateFlat(
            @PathVariable Long id,
            @Valid @RequestBody FlatDTO flatDto,
            @AuthenticationPrincipal User currentUser) {
        
        FlatDTO updatedFlat = flatService.updateFlat(id, flatDto);
        return ResponseEntity.ok(updatedFlat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlat(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        flatService.deleteFlat(id);
        return ResponseEntity.noContent().build();
    }
}