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

import com.app.dto.FlatMemberDTO;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.FlatMemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flat-members")
@RequiredArgsConstructor
public class FlatMemberController {

    private final FlatMemberService flatMemberService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlatMemberDTO>> getAllFlatMembers(@AuthenticationPrincipal User currentUser) {
        List<FlatMemberDTO> flatMembers = flatMemberService.getAllFlatMembers();
        return ResponseEntity.ok(flatMembers);
    }

    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<FlatMemberDTO>> getFlatMembersByFlatId(
            @PathVariable Long flatId,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the flat belongs to the user's society
        
        List<FlatMemberDTO> flatMembers = flatMemberService.getFlatMembersByFlatId(flatId);
        return ResponseEntity.ok(flatMembers);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FlatMemberDTO>> getFlatMembersByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure users can only access their own flat memberships unless they are admins
        if (!currentUser.getId().equals(userId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<FlatMemberDTO> flatMembers = flatMemberService.getFlatMembersByUserId(userId);
        return ResponseEntity.ok(flatMembers);
    }

    @GetMapping("/pending/{societyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FlatMemberDTO>> getPendingFlatMembersBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only access pending members from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<FlatMemberDTO> pendingMembers = flatMemberService.getPendingFlatMembersBySocietyId(societyId);
        return ResponseEntity.ok(pendingMembers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlatMemberDTO> getFlatMemberById(@PathVariable Long id) {
        FlatMemberDTO flatMember = flatMemberService.getFlatMemberById(id);
        return ResponseEntity.ok(flatMember);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public ResponseEntity<FlatMemberDTO> createFlatMember(
            @Valid @RequestBody FlatMemberDTO flatMemberDto,
            @AuthenticationPrincipal User currentUser) {
        
        FlatMemberDTO createdFlatMember = flatMemberService.createFlatMember(flatMemberDto, currentUser.getId());
        return new ResponseEntity<>(createdFlatMember, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public ResponseEntity<FlatMemberDTO> updateFlatMember(
            @PathVariable Long id,
            @Valid @RequestBody FlatMemberDTO flatMemberDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the user has permission to update this flat member
        
        FlatMemberDTO updatedFlatMember = flatMemberService.updateFlatMember(id, flatMemberDto);
        return ResponseEntity.ok(updatedFlatMember);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FlatMemberDTO> approveFlatMember(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        FlatMemberDTO approvedFlatMember = flatMemberService.approveFlatMember(id, currentUser.getId());
        return ResponseEntity.ok(approvedFlatMember);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESIDENT')")
    public ResponseEntity<Void> deleteFlatMember(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the user has permission to delete this flat member
        
        flatMemberService.deleteFlatMember(id);
        return ResponseEntity.noContent().build();
    }
}
