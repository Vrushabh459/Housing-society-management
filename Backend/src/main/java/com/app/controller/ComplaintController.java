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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.dto.ComplaintDTO;
import com.app.model.ComplaintStatus;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public ResponseEntity<List<ComplaintDTO>> getAllComplaints(@AuthenticationPrincipal User currentUser) {
        // If user is an admin, return complaints from their society
        if (currentUser.getRole() == UserRole.ADMIN && currentUser.getSociety() != null) {
            List<ComplaintDTO> complaints = complaintService.getComplaintsBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(complaints);
        } 
        // If user is a resident, return only their complaints
        else if (currentUser.getRole() == UserRole.RESIDENT) {
            List<ComplaintDTO> complaints = complaintService.getComplaintsByUserId(currentUser.getId());
            return ResponseEntity.ok(complaints);
        }
        // Super admin can see all complaints
        else if (currentUser.getRole() == UserRole.ADMIN && currentUser.getSociety() == null) {
            List<ComplaintDTO> complaints = complaintService.getAllComplaints();
            return ResponseEntity.ok(complaints);
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/flat/{flatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public ResponseEntity<List<ComplaintDTO>> getComplaintsByFlatId(
            @PathVariable Long flatId,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the flat belongs to the user's society or the user
        
        List<ComplaintDTO> complaints = complaintService.getComplaintsByFlatId(flatId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ComplaintDTO>> getComplaintsByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure users can only access their own complaints unless they are admins
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ComplaintDTO> complaints = complaintService.getComplaintsByUserId(userId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/society/{societyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintDTO>> getComplaintsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only access complaints from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ComplaintDTO> complaints = complaintService.getComplaintsBySocietyId(societyId);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/society/{societyId}/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ComplaintDTO>> getComplaintsBySocietyIdAndStatus(
            @PathVariable Long societyId,
            @PathVariable ComplaintStatus status,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only access complaints from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<ComplaintDTO> complaints = complaintService.getComplaintsBySocietyIdAndStatus(societyId, status);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDTO> getComplaintById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        ComplaintDTO complaint = complaintService.getComplaintById(id);
        
        // Ensure users can only access their own complaints or admins can access complaints from their society
        boolean isOwner = complaint.getRaisedById().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;
        
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(complaint);
    }

    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<ComplaintDTO> createComplaint(
            @Valid @RequestBody ComplaintDTO complaintDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Set the current user as the one who raised the complaint
        complaintDto.setRaisedById(currentUser.getId());
        complaintDto.setRaisedByName(currentUser.getName());
        
        ComplaintDTO createdComplaint = complaintService.createComplaint(complaintDto);
        return new ResponseEntity<>(createdComplaint, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplaintDTO> updateComplaintStatus(
            @PathVariable Long id,
            @RequestParam ComplaintStatus status,
            @RequestParam(required = false) String resolution,
            @AuthenticationPrincipal User currentUser) {
        
        ComplaintDTO updatedComplaint = complaintService.updateComplaintStatus(id, status, resolution, currentUser.getId());
        return ResponseEntity.ok(updatedComplaint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintDTO> updateComplaint(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO complaintDto,
            @AuthenticationPrincipal User currentUser) {
        
        ComplaintDTO existingComplaint = complaintService.getComplaintById(id);
        
        // Ensure users can only update their own complaints
        if (!existingComplaint.getRaisedById().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        ComplaintDTO updatedComplaint = complaintService.updateComplaint(id, complaintDto);
        return ResponseEntity.ok(updatedComplaint);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        ComplaintDTO existingComplaint = complaintService.getComplaintById(id);
        
        // Ensure users can only delete their own complaints or admins can delete any complaint
        if (!existingComplaint.getRaisedById().equals(currentUser.getId()) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
}
