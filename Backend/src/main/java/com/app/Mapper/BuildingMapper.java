package com.app.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


import com.app.dto.BuildingDTO;
import com.app.model.Building;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    
    @Mapping(source = "building", target = "societyId", qualifiedByName = "getSocietyId")
    @Mapping(source = "building", target = "societyName", qualifiedByName = "getSocietyName")
    BuildingDTO toDTO(Building building);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "society", ignore = true)
    @Mapping(target = "flats", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Building toEntity(BuildingDTO dto);
    
    @Named("getSocietyId")
    default Long getSocietyId(Building building) {
        return building != null && building.getSociety() != null ? building.getSociety().getId() : null;
    }
    
    @Named("getSocietyName")
    default String getSocietyName(Building building) {
        return building != null && building.getSociety() != null ? building.getSociety().getName() : null;
    }
    List<BuildingDTO> toDtoList(List<Building> buildings);
}

