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

import com.app.dto.BuildingDTO;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.BuildingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    public ResponseEntity<List<BuildingDTO>> getAllBuildings(@AuthenticationPrincipal User currentUser) {
        // If user is an admin, resident, or guard, only return buildings from their society
        if (currentUser.getSociety() != null) {
            List<BuildingDTO> buildings = buildingService.getBuildingsBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(buildings);
        } else {
            // Super admin can see all buildings
            List<BuildingDTO> buildings = buildingService.getAllBuildings();
            return ResponseEntity.ok(buildings);
        }
    }

    @GetMapping("/society/{societyId}")
    public ResponseEntity<List<BuildingDTO>> getBuildingsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access buildings from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<BuildingDTO> buildings = buildingService.getBuildingsBySocietyId(societyId);
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDTO> getBuildingById(@PathVariable Long id) {
        BuildingDTO building = buildingService.getBuildingById(id);
        return ResponseEntity.ok(building);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingDTO> createBuilding(
            @Valid @RequestBody BuildingDTO buildingDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only create buildings in their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(buildingDto.getSocietyId()) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        BuildingDTO createdBuilding = buildingService.createBuilding(buildingDto);
        return new ResponseEntity<>(createdBuilding, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BuildingDTO> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingDTO buildingDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only update buildings in their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(buildingDto.getSocietyId()) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        BuildingDTO updatedBuilding = buildingService.updateBuilding(id, buildingDto);
        return ResponseEntity.ok(updatedBuilding);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBuilding(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only delete buildings in their own society
        BuildingDTO building = buildingService.getBuildingById(id);
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(building.getSocietyId()) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
