package com.app.service;

import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.FlatAllocationRequestMapper;
import com.app.dao.FlatAllocationDao;
import com.app.dao.FlatDao;
import com.app.dao.UserDao;
import com.app.dto.FlatAllocationRequestDTO;
import com.app.dto.NotificationDto;
import com.app.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlatAllocationServiceImpl implements FlatAllocationService {

    private final FlatAllocationDao allocationRepository;
    private final FlatDao flatRepository;
    private final UserDao userRepository;
    private final FlatAllocationRequestMapper allocationMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public FlatAllocationRequestDTO createAllocationRequest(FlatAllocationRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Flat flat = flatRepository.findById(requestDTO.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + requestDTO.getFlatId()));

        FlatAllocation allocation = new FlatAllocation();
        allocation.setUser(user);
        allocation.setFlat(flat);
        allocation.setStatus(AllocationStatus.PENDING);
        allocation.setResidentType(requestDTO.getResidentType());
        allocation.setOccupation(requestDTO.getOccupation());
        allocation.setFamilyMembers(requestDTO.getFamilyMembers());

        FlatAllocation savedAllocation = allocationRepository.save(allocation);

        // Notify admins
        NotificationDto notification = NotificationDto.create(
                "NEW_ALLOCATION_REQUEST",
                "New flat allocation request from " + user.getName() + " for flat " + flat.getFlatNumber(),
                allocationMapper.toDTO(savedAllocation),
                userId,
                user.getName(),
                null, // No specific recipient, goes to admin topic
                flat.getBuilding().getSociety().getId()
        );
        notificationService.sendAdminNotification(notification);

        return allocationMapper.toDTO(savedAllocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlatAllocationRequestDTO> getAllocationRequestsBySociety(Long societyId) {
        return allocationRepository.findBySocietyId(societyId).stream()
                .map(allocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlatAllocationRequestDTO approveAllocationRequest(Long requestId, Long adminId) {
        FlatAllocation allocation = allocationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation request not found with id: " + requestId));
        
        allocation.setStatus(AllocationStatus.APPROVED);
        
        // Update flat status
        Flat flat = allocation.getFlat();
        flat.setOccupiedStatus(OccupiedStatus.OCCUPIED);
        flatRepository.save(flat);

        FlatAllocation updatedAllocation = allocationRepository.save(allocation);
        return allocationMapper.toDTO(updatedAllocation);
    }

    @Override
    @Transactional
    public FlatAllocationRequestDTO rejectAllocationRequest(Long requestId, Long adminId) {
        FlatAllocation allocation = allocationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation request not found with id: " + requestId));
        
        allocation.setStatus(AllocationStatus.REJECTED);
        FlatAllocation updatedAllocation = allocationRepository.save(allocation);
        return allocationMapper.toDTO(updatedAllocation);
    }
}
