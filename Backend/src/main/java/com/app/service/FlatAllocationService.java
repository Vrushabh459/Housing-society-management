package com.app.service;

import com.app.dto.FlatAllocationRequestDTO;

import java.util.List;

public interface FlatAllocationService {
    FlatAllocationRequestDTO createAllocationRequest(FlatAllocationRequestDTO requestDTO, Long userId);
    List<FlatAllocationRequestDTO> getAllocationRequestsBySociety(Long societyId);
    FlatAllocationRequestDTO approveAllocationRequest(Long requestId, Long adminId);
    FlatAllocationRequestDTO rejectAllocationRequest(Long requestId, Long adminId);
}
