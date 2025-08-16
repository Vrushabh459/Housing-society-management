package com.app.controller;

import com.app.dto.FlatAllocationRequestDTO;
import com.app.model.User;
import com.app.service.FlatAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/allocation-requests")
@RequiredArgsConstructor
public class FlatAllocationController {

    private final FlatAllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<FlatAllocationRequestDTO> createAllocationRequest(
            @Valid @RequestBody FlatAllocationRequestDTO requestDTO,
            @AuthenticationPrincipal User currentUser) {
        FlatAllocationRequestDTO createdRequest = allocationService.createAllocationRequest(requestDTO, currentUser.getId());
        return new ResponseEntity<>(createdRequest, HttpStatus.CREATED);
    }

    @GetMapping("/society/{societyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlatAllocationRequestDTO>> getRequestsBySociety(@PathVariable Long societyId) {
        List<FlatAllocationRequestDTO> requests = allocationService.getAllocationRequestsBySociety(societyId);
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlatAllocationRequestDTO> approveRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        FlatAllocationRequestDTO approvedRequest = allocationService.approveAllocationRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(approvedRequest);
    }

    @PutMapping("/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlatAllocationRequestDTO> rejectRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal User currentUser) {
        FlatAllocationRequestDTO rejectedRequest = allocationService.rejectAllocationRequest(requestId, currentUser.getId());
        return ResponseEntity.ok(rejectedRequest);
    }
}