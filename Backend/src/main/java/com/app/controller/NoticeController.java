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

import com.app.dto.NoticeDTO;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.NoticeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAllNotices(@AuthenticationPrincipal User currentUser) {
        // If user is an admin, return all notices
        if (currentUser.getRole() == UserRole.ADMIN && currentUser.getSociety() == null) {
            List<NoticeDTO> notices = noticeService.getAllNotices();
            return ResponseEntity.ok(notices);
        } else {
            // Otherwise, return notices from their society
            List<NoticeDTO> notices = noticeService.getNoticesBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(notices);
        }
    }

    @GetMapping("/society/{societyId}")
    public ResponseEntity<List<NoticeDTO>> getNoticesBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access notices from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<NoticeDTO> notices = noticeService.getNoticesBySocietyId(societyId);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/society/{societyId}/active")
    public ResponseEntity<List<NoticeDTO>> getActiveNoticesBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure user can only access notices from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<NoticeDTO> notices = noticeService.getActiveNoticesBySocietyId(societyId);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDTO> getNoticeById(@PathVariable Long id) {
        NoticeDTO notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeDTO> createNotice(
            @Valid @RequestBody NoticeDTO noticeDto,
            @AuthenticationPrincipal User currentUser) {
        
        // Set the current user as the creator of the notice
        noticeDto.setCreatedById(currentUser.getId());
        noticeDto.setCreatedByName(currentUser.getName());
        
        // Set the society ID if not provided
        if (noticeDto.getSocietyId() == null && currentUser.getSociety() != null) {
            noticeDto.setSocietyId(currentUser.getSociety().getId());
            noticeDto.setSocietyName(currentUser.getSociety().getName());
        }
        
        NoticeDTO createdNotice = noticeService.createNotice(noticeDto);
        return new ResponseEntity<>(createdNotice, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeDTO> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeDTO noticeDto,
            @AuthenticationPrincipal User currentUser) {
        
        NoticeDTO existingNotice = noticeService.getNoticeById(id);
        
        // Ensure admin can only update notices from their own society
        if (currentUser.getSociety() != null && 
            !currentUser.getSociety().getId().equals(existingNotice.getSocietyId()) && 
            currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        NoticeDTO updatedNotice = noticeService.updateNotice(id, noticeDto);
        return ResponseEntity.ok(updatedNotice);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeDTO> deactivateNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        NoticeDTO existingNotice = noticeService.getNoticeById(id);
        
        // Ensure admin can only deactivate notices from their own society
        if (currentUser.getSociety() != null && 
            !currentUser.getSociety().getId().equals(existingNotice.getSocietyId()) && 
            currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        NoticeDTO deactivatedNotice = noticeService.deactivateNotice(id);
        return ResponseEntity.ok(deactivatedNotice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        NoticeDTO existingNotice = noticeService.getNoticeById(id);
        
        // Ensure admin can only delete notices from their own society
        if (currentUser.getSociety() != null && 
            !currentUser.getSociety().getId().equals(existingNotice.getSocietyId()) && 
            currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
