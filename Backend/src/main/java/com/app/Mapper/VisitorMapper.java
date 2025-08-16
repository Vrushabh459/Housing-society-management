package com.app.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import com.app.dto.VisitorDTO;
import com.app.model.Visitor;

@Mapper(componentModel = "spring")
public interface VisitorMapper {
    
	@Mapping(source = "visitingFlat.id", target = "flatId")
    @Mapping(source = "visitingFlat.flatNumber", target = "flatNumber")
    @Mapping(source = "loggedBy.id", target = "loggedById")
    @Mapping(source = "loggedBy.name", target = "loggedByName")
    @Mapping(source = "approvedBy.id", target = "approvedById")
    @Mapping(source = "approvedBy.name", target = "approvedByName")
    VisitorDTO toDTO(Visitor visitorLog);
    
    List<VisitorDTO> toDtoList(List<Visitor> visitors);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "flatId", target = "visitingFlat.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "loggedById", target = "loggedBy.id")
    @Mapping(source = "approvedById", target = "approvedBy.id")
    @Mapping(target = "approvalTime", ignore = true)
    Visitor toEntity(VisitorDTO dto);
    
    @Named("getFlatId")
    default Long getFlatId(Visitor visitorLog) {
        return visitorLog != null && visitorLog.getVisitingFlat() != null ? visitorLog.getVisitingFlat().getId() : null;
    }
    
    @Named("getFlatNumber")
    default String getFlatNumber(Visitor visitorLog) {
        return visitorLog != null && visitorLog.getVisitingFlat() != null ? visitorLog.getVisitingFlat().getFlatNumber() : null;
    }
    
  
}
