package com.app.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceAlreadyExistsException;
import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.MaintenanceBillMapper;
import com.app.dao.FlatDao;
import com.app.dao.FlatMemberDao;
import com.app.dao.MaintenanceBillDao;
import com.app.dao.UserDao;
import com.app.dto.MaintenanceBillDTO;
import com.app.dto.NotificationDto;
import com.app.model.Flat;
import com.app.model.FlatMember;
import com.app.model.MaintenanceBill;
import com.app.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaintenanceBillService {

    private final MaintenanceBillDao maintenanceBillRepository;
    private final FlatDao flatRepository;
    private final FlatMemberDao flatMemberRepository;
    private final UserDao userRepository;
    private final MaintenanceBillMapper maintenanceBillMapper;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<MaintenanceBillDTO> getAllMaintenanceBills() {
        List<MaintenanceBill> bills = maintenanceBillRepository.findAll();
        return maintenanceBillMapper.toDtoList(bills);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceBillDTO> getMaintenanceBillsByFlatId(Long flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException("Flat not found with id: " + flatId);
        }
        List<MaintenanceBill> bills = maintenanceBillRepository.findByFlatId(flatId);
        return maintenanceBillMapper.toDtoList(bills);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceBillDTO> getMaintenanceBillsBySocietyId(Long societyId) {
        List<MaintenanceBill> bills = maintenanceBillRepository.findBySocietyId(societyId);
        return maintenanceBillMapper.toDtoList(bills);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceBillDTO> getPendingMaintenanceBillsBySocietyId(Long societyId) {
        List<MaintenanceBill> bills = maintenanceBillRepository.findBySocietyIdAndPaid(societyId, false);
        return maintenanceBillMapper.toDtoList(bills);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceBillDTO> getOverdueMaintenanceBills() {
        List<MaintenanceBill> bills = maintenanceBillRepository.findByDueDateBeforeAndPaid(LocalDate.now(), false);
        return maintenanceBillMapper.toDtoList(bills);
    }

    @Transactional(readOnly = true)
    public MaintenanceBillDTO getMaintenanceBillById(Long id) {
        MaintenanceBill bill = maintenanceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));
        return maintenanceBillMapper.toDTO(bill);
    }

    @Transactional
    public MaintenanceBillDTO createMaintenanceBill(MaintenanceBillDTO billDto, Long adminUserId) {
        Flat flat = flatRepository.findById(billDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + billDto.getFlatId()));

        // Generate unique bill number
        String billNumber = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Check if bill number already exists
        if (maintenanceBillRepository.findByBillNumber(billNumber).isPresent()) {
            throw new ResourceAlreadyExistsException("Bill with number " + billNumber + " already exists");
        }

        MaintenanceBill bill = MaintenanceBill.builder()
                .billNumber(billNumber)
                .billDate(billDto.getBillDate() != null ? billDto.getBillDate() : LocalDate.now())
                .dueDate(billDto.getDueDate())
                .amount(billDto.getAmount())
                .paid(false)
                .flat(flat)
                .description(billDto.getDescription())
                .build();

        MaintenanceBill savedBill = maintenanceBillRepository.save(bill);
        MaintenanceBillDTO savedDto = maintenanceBillMapper.toDTO(savedBill);

        // Get admin name
        String adminName = "Admin";
        if (adminUserId != null) {
            User admin = userRepository.findById(adminUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + adminUserId));
            adminName = admin.getName();
        }

        // Send notification to flat owners
        List<FlatMember> owners = flatMemberRepository.findByFlatIdAndIsOwner(flat.getId(), true);
        for (FlatMember owner : owners) {
            if (owner.getUser() != null) {
                NotificationDto notification = NotificationDto.create(
                        "NEW_MAINTENANCE_BILL",
                        "New maintenance bill generated for flat " + flat.getFlatNumber(),
                        savedDto,
                        adminUserId,
                        adminName,
                        owner.getUser().getId(),
                        flat.getBuilding().getSociety().getId()
                );
                notificationService.sendPrivateNotification(notification);
            }
        }

        return savedDto;
    }

    @Transactional
    public MaintenanceBillDTO updateMaintenanceBill(Long id, MaintenanceBillDTO billDto) {
        MaintenanceBill bill = maintenanceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));

        Flat flat = flatRepository.findById(billDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + billDto.getFlatId()));

        bill.setBillDate(billDto.getBillDate());
        bill.setDueDate(billDto.getDueDate());
        bill.setAmount(billDto.getAmount());
        bill.setFlat(flat);
        bill.setDescription(billDto.getDescription());

        MaintenanceBill updatedBill = maintenanceBillRepository.save(bill);
        return maintenanceBillMapper.toDTO(updatedBill);
    }

    @Transactional
    public MaintenanceBillDTO markBillAsPaid(Long id, String paymentReference, Long userId) {
        MaintenanceBill bill = maintenanceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance bill not found with id: " + id));

        bill.setPaid(true);
        bill.setPaymentDate(LocalDate.now());
        bill.setPaymentReference(paymentReference);

        MaintenanceBill updatedBill = maintenanceBillRepository.save(bill);
        MaintenanceBillDTO updatedDto = maintenanceBillMapper.toDTO(updatedBill);

        // Get user name
        String userName = "Resident";
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            userName = user.getName();
        }

        // Send notification to admins
        NotificationDto notification = NotificationDto.create(
                "MAINTENANCE_BILL_PAID",
                "Maintenance bill " + bill.getBillNumber() + " has been paid",
                updatedDto,
                userId,
                userName,
                null,
                bill.getFlat().getBuilding().getSociety().getId()
        );
        notificationService.sendAdminNotification(notification);

        return updatedDto;
    }

    @Transactional
    public void deleteMaintenanceBill(Long id) {
        if (!maintenanceBillRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance bill not found with id: " + id);
        }
        maintenanceBillRepository.deleteById(id);
    }

    @Transactional
    public List<MaintenanceBillDTO> generateBulkMaintenanceBills(Long societyId, LocalDate billDate, LocalDate dueDate, String description, Long adminUserId) {
        // Get all flats in the society
        List<Flat> flats = flatRepository.findBySocietyId(societyId);
        
        List<MaintenanceBill> generatedBills = flats.stream().map(flat -> {
            // Calculate amount based on flat area (example calculation)
            double amount = flat.getArea() * 2.5; // Rs. 2.5 per sq ft
            
            // Generate unique bill number
            String billNumber = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            MaintenanceBill bill = MaintenanceBill.builder()
                    .billNumber(billNumber)
                    .billDate(billDate)
                    .dueDate(dueDate)
                    .amount(BigDecimal.valueOf(amount))
                    .paid(false)
                    .flat(flat)
                    .description(description)
                    .build();
            
            return maintenanceBillRepository.save(bill);
        }).collect(Collectors.toList());
        
        List<MaintenanceBillDTO> generatedBillDtos = maintenanceBillMapper.toDtoList(generatedBills);
        
        // Get admin name
        String adminName = "Admin";
        if (adminUserId != null) {
            User admin = userRepository.findById(adminUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + adminUserId));
            adminName = admin.getName();
        }
        
        // Send notification to all residents
        NotificationDto notification = NotificationDto.create(
                "BULK_MAINTENANCE_BILLS_GENERATED",
                "New maintenance bills have been generated for all flats",
                generatedBillDtos,
                adminUserId,
                adminName,
                null,
                societyId
        );
        notificationService.sendResidentNotification(notification);
        
        return generatedBillDtos;
    }
}
