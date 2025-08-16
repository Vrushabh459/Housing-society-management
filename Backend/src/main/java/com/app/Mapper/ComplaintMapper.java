package com.app.Mapper;

import java.util.List;

import org.mapstruct.*;

import com.app.dto.ComplaintDTO;
import com.app.model.Complaint;

@Mapper(componentModel = "spring")
public interface ComplaintMapper {

	@Mapping(source = "flat.id", target = "flatId")
    @Mapping(source = "flat.flatNumber", target = "flatNumber")
    @Mapping(source = "createdBy.id", target = "raisedById")
    @Mapping(source = "createdBy.name", target = "raisedByName")
    ComplaintDTO toDTO(Complaint complaint);
    
    List<ComplaintDTO> toDtoList(List<Complaint> complaints);
    
    @Mapping(source = "flatId", target = "flat.id")
    @Mapping(source = "raisedById", target = "createdBy.id")
    Complaint toEntity(ComplaintDTO complaintDto);
    
    @Named("getUserName")
    default String getUserName(Complaint complaint) {
        return complaint != null && complaint.getCreatedBy() != null ? complaint.getCreatedBy().getName() : null;
    }
    
    @Named("getUserEmail")
    default String getUserEmail(Complaint complaint) {
        return complaint != null && complaint.getCreatedBy() != null ? complaint.getCreatedBy().getEmail() : null;
    }
    
    @Named("getFlatNumber")
    default String getFlatNumber(Complaint complaint) {
        return complaint != null && complaint.getCreatedBy() != null && 
               complaint.getCreatedBy().getFlat() != null ? complaint.getCreatedBy().getFlat().getFlatNumber() : null;
    }
}
