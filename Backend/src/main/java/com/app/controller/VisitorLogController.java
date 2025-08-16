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

import com.app.dto.VisitorDTO;
import com.app.model.User;
import com.app.service.VisitorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/visitor-logs")
@RequiredArgsConstructor
public class VisitorLogController {

    private final VisitorService visitorLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ResponseEntity<List<VisitorDTO>> getAllVisitorLogs(@AuthenticationPrincipal User currentUser) {
        // If user is an admin or guard, return visitor logs from their society
        if (currentUser.getSociety() != null) {
            List<VisitorDTO> visitorLogs = visitorLogService.getVisitorLogsBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(visitorLogs);
        } else {
            // Super admin can see all visitor logs
            List<VisitorDTO> visitorLogs = visitorLogService.getAllVisitors();
            return ResponseEntity.ok(visitorLogs);
        }
    }

    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<VisitorDTO>> getVisitorLogsByFlatId(
            @PathVariable Long flatId,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the flat belongs to the user's society or the user
        
        List<VisitorDTO> visitorLogs = visitorLogService.getVisitorLogsByFlatId(flatId);
        return ResponseEntity.ok(visitorLogs);
    }

    @GetMapping("/society/{societyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ResponseEntity<List<VisitorDTO>> getVisitorLogsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access visitor logs from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<VisitorDTO> visitorLogs = visitorLogService.getVisitorLogsBySocietyId(societyId);
        return ResponseEntity.ok(visitorLogs);
    }

    @GetMapping("/society/{societyId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ResponseEntity<List<VisitorDTO>> getActiveVisitorsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access visitor logs from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<VisitorDTO> visitorLogs = visitorLogService.getActiveVisitorsBySocietyId(societyId);
        return ResponseEntity.ok(visitorLogs);
    }

    @GetMapping("/society/{societyId}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ResponseEntity<List<VisitorDTO>> getPendingApprovalVisitorLogs(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access visitor logs from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<VisitorDTO> visitorLogs = visitorLogService.getPendingApprovalVisitorLogs(societyId);
        return ResponseEntity.ok(visitorLogs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorDTO> getVisitorLogById(@PathVariable Long id) {
        VisitorDTO visitorLog = visitorLogService.getVisitorById(id);
        return ResponseEntity.ok(visitorLog);
    }

    @PostMapping
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<VisitorDTO> createVisitorLog(
            @Valid @RequestBody VisitorDTO visitorLogDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Set the current user as the one who logged the visitor
        visitorLogDto.setLoggedById(currentUser.getId());
        visitorLogDto.setLoggedByName(currentUser.getName());
        
        VisitorDTO createdVisitorLog = visitorLogService.createVisitorLog(visitorLogDto);
        return new ResponseEntity<>(createdVisitorLog, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<VisitorDTO> approveVisitorLog(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the visitor is for the user's flat
        
        VisitorDTO approvedVisitorLog = visitorLogService.approveVisitorLog(id, currentUser.getId());
        return ResponseEntity.ok(approvedVisitorLog);
    }

    @PutMapping("/{id}/exit")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<VisitorDTO> recordVisitorExit(@PathVariable Long id) {
        VisitorDTO updatedVisitorLog = visitorLogService.recordVisitorExit(id);
        return ResponseEntity.ok(updatedVisitorLog);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GUARD')")
    public ResponseEntity<VisitorDTO> updateVisitorLog(
            @PathVariable Long id,
            @Valid @RequestBody VisitorDTO visitorLogDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the user has permission to update this visitor log
        
        VisitorDTO updatedVisitorLog = visitorLogService.updateVisitorLog(id, visitorLogDto);
        return ResponseEntity.ok(updatedVisitorLog);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARD')")
    public ResponseEntity<Void> deleteVisitorLog(@PathVariable Long id) {
        visitorLogService.deleteVisitor(id);
        return ResponseEntity.noContent().build();
    }
}
