package com.app.Mapper;

import java.util.List;

import org.mapstruct.*;

import com.app.dto.MaintenanceBillDTO;
import com.app.model.MaintenanceBill;

@Mapper(componentModel = "spring")
public interface MaintenanceBillMapper {
    
    @Mapping(source = "bill", target = "flatId", qualifiedByName = "getFlatId")
    @Mapping(source = "bill", target = "flatNumber", qualifiedByName = "getFlatNumber")
    MaintenanceBillDTO toDTO(MaintenanceBill bill);
    
    List<MaintenanceBillDTO> toDtoList(List<MaintenanceBill> bills);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "flat", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "paid", ignore = true)
    MaintenanceBill toEntity(MaintenanceBillDTO dto);
    
    @Named("getFlatId")
    default Long getFlatId(MaintenanceBill bill) {
        return bill != null && bill.getFlat() != null ? bill.getFlat().getId() : null;
    }
    
    @Named("getFlatNumber")
    default String getFlatNumber(MaintenanceBill bill) {
        return bill != null && bill.getFlat() != null ? bill.getFlat().getFlatNumber() : null;
    }
}
