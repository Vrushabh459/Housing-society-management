package com.app.Mapper;

import org.mapstruct.*;

import com.app.dto.FlatAllocationRequestDTO;
import com.app.model.FlatAllocation;

@Mapper(componentModel = "spring")
public interface FlatAllocationRequestMapper {
    
    @Mapping(source = "request", target = "flatId", qualifiedByName = "getFlatId")
    @Mapping(source = "request", target = "flatNumber", qualifiedByName = "getFlatNumber")
    @Mapping(source = "request", target = "userName", qualifiedByName = "getUserName")
    @Mapping(source = "request", target = "userEmail", qualifiedByName = "getUserEmail")
    FlatAllocationRequestDTO toDTO(FlatAllocation request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "flat", ignore = true)
    FlatAllocation toEntity(FlatAllocationRequestDTO dto);
    
    @Named("getFlatId")
    default Long getFlatId(FlatAllocation request) {
        return request != null && request.getFlat() != null ? request.getFlat().getId() : null;
    }
    
    @Named("getFlatNumber")
    default String getFlatNumber(FlatAllocation request) {
        return request != null && request.getFlat() != null ? request.getFlat().getFlatNumber() : null;
    }
    
    @Named("getUserName")
    default String getUserName(FlatAllocation request) {
        return request != null && request.getUser() != null ? request.getUser().getName() : null;
    }
    
    @Named("getUserEmail")
    default String getUserEmail(FlatAllocation request) {
        return request != null && request.getUser() != null ? request.getUser().getEmail() : null;
    }
}

