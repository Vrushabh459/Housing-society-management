package com.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.app.dto.MaintenanceBillDTO;
import com.app.model.User;
import com.app.model.UserRole;
import com.app.service.MaintenanceBillService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/maintenance-bills")
@RequiredArgsConstructor
public class MaintenanceBillController {

    private final MaintenanceBillService maintenanceBillService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceBillDTO>> getAllMaintenanceBills(@AuthenticationPrincipal User currentUser) {
        // If user is an admin, return bills from their society
        if (currentUser.getSociety() != null) {
            List<MaintenanceBillDTO> bills = maintenanceBillService.getMaintenanceBillsBySocietyId(currentUser.getSociety().getId());
            return ResponseEntity.ok(bills);
        } else {
            // Super admin can see all bills
            List<MaintenanceBillDTO> bills = maintenanceBillService.getAllMaintenanceBills();
            return ResponseEntity.ok(bills);
        }
    }

    @GetMapping("/flat/{flatId}")
    public ResponseEntity<List<MaintenanceBillDTO>> getMaintenanceBillsByFlatId(
            @PathVariable Long flatId,
            @AuthenticationPrincipal User currentUser) {
        
        // Additional validation can be added here to ensure the flat belongs to the user's society or the user
        
        List<MaintenanceBillDTO> bills = maintenanceBillService.getMaintenanceBillsByFlatId(flatId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/society/{societyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceBillDTO>> getMaintenanceBillsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only access bills from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<MaintenanceBillDTO> bills = maintenanceBillService.getMaintenanceBillsBySocietyId(societyId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/society/{societyId}/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceBillDTO>> getPendingMaintenanceBillsBySocietyId(
            @PathVariable Long societyId,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only access bills from their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<MaintenanceBillDTO> bills = maintenanceBillService.getPendingMaintenanceBillsBySocietyId(societyId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceBillDTO>> getOverdueMaintenanceBills() {
        List<MaintenanceBillDTO> bills = maintenanceBillService.getOverdueMaintenanceBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceBillDTO> getMaintenanceBillById(@PathVariable Long id) {
        MaintenanceBillDTO bill = maintenanceBillService.getMaintenanceBillById(id);
        return ResponseEntity.ok(bill);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceBillDTO> createMaintenanceBill(
            @Valid @RequestBody MaintenanceBillDTO billDto,
            @AuthenticationPrincipal User currentUser) {
        
        MaintenanceBillDTO createdBill = maintenanceBillService.createMaintenanceBill(billDto, currentUser.getId());
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @PostMapping("/bulk/{societyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MaintenanceBillDTO>> generateBulkMaintenanceBills(
            @PathVariable Long societyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate billDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam String description,
            @AuthenticationPrincipal User currentUser) {
        
        // Ensure admin can only generate bills for their own society
        if (currentUser.getSociety() != null && !currentUser.getSociety().getId().equals(societyId) && currentUser.getRole() == UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<MaintenanceBillDTO> generatedBills = maintenanceBillService.generateBulkMaintenanceBills(
                societyId, billDate, dueDate, description, currentUser.getId());
        return new ResponseEntity<>(generatedBills, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MaintenanceBillDTO> updateMaintenanceBill(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceBillDTO billDto,
            @AuthenticationPrincipal User currentUser) {
        
        MaintenanceBillDTO updatedBill = maintenanceBillService.updateMaintenanceBill(id, billDto);
        return ResponseEntity.ok(updatedBill);
    }

    @PutMapping("/{id}/pay")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<MaintenanceBillDTO> markBillAsPaid(
            @PathVariable Long id,
            @RequestParam String paymentReference,
            @AuthenticationPrincipal User currentUser) {
        
        MaintenanceBillDTO paidBill = maintenanceBillService.markBillAsPaid(id, paymentReference, currentUser.getId());
        return ResponseEntity.ok(paidBill);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMaintenanceBill(@PathVariable Long id) {
        maintenanceBillService.deleteMaintenanceBill(id);
        return ResponseEntity.noContent().build();
    }
}
