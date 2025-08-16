package com.app.Mapper;



import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.SocietyCreationRequest;
import com.app.dto.SocietyDTO;
import com.app.model.Society;

@Mapper(componentModel = "spring")
public interface SocietyMapper {
    SocietyDTO toDTO(Society society);
    List<SocietyDTO> toDtoList(List<Society> societies);
    
    @Mapping(target = "buildings", ignore = true)
    @Mapping(target = "admins", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Society toEntity(SocietyDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "buildings", ignore = true)
    @Mapping(target = "admins", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Society toEntity(SocietyCreationRequest societyCreationRequest);
	
}
