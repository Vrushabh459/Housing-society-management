package com.app.Mapper;

import java.util.List;
import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.app.dto.FlatDTO;
import com.app.model.Flat;
import com.app.model.FlatMember;
import com.app.model.User;

@Mapper(componentModel = "spring")
public interface FlatMapper {

    @Mapping(source = "flat.building.id", target = "buildingId")
    @Mapping(source = "flat.building.name", target = "buildingName")
    @Mapping(source = "flat.building.society.name", target = "societyName")
    @Mapping(source = "members", target = "ownerId", qualifiedByName = "getOwnerId")
    @Mapping(source = "members", target = "ownerName", qualifiedByName = "getOwnerName")
    @Mapping(source = "members", target = "ownerEmail", qualifiedByName = "getOwnerEmail")
    @Mapping(source = "members", target = "ownerPhone", qualifiedByName = "getOwnerPhone")
    @Mapping(source = "members", target = "totalMembers", qualifiedByName = "getTotalMembers")
    FlatDTO toDTO(Flat flat);

    List<FlatDTO> toDtoList(List<Flat> flats);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "maintenanceBills", ignore = true)
    @Mapping(target = "flatAllocations", ignore = true)
    @Mapping(target = "complaints", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "buildingId", target = "building.id")
    @Mapping(source = "buildingName", target = "building.name")
    Flat toEntity(FlatDTO dto);

    // --- HELPER METHODS TO EXTRACT OWNER DETAILS ---

    default Optional<User> getOwnerUser(java.util.Set<FlatMember> members) {
        if (members == null || members.isEmpty()) {
            return Optional.empty();
        }
        return members.stream()
                .filter(FlatMember::isOwner)
                .findFirst()
                .map(FlatMember::getUser);
    }

    @Named("getOwnerId")
    default Long getOwnerId(java.util.Set<FlatMember> members) {
        return getOwnerUser(members).map(User::getId).orElse(null);
    }

    @Named("getOwnerName")
    default String getOwnerName(java.util.Set<FlatMember> members) {
        return getOwnerUser(members).map(User::getName).orElse(null);
    }
    
    @Named("getOwnerEmail")
    default String getOwnerEmail(java.util.Set<FlatMember> members) {
        return getOwnerUser(members).map(User::getEmail).orElse(null);
    }

    @Named("getOwnerPhone")
    default String getOwnerPhone(java.util.Set<FlatMember> members) {
        return getOwnerUser(members).map(User::getPhone).orElse(null);
    }

    @Named("getTotalMembers")
    default Integer getTotalMembers(java.util.Set<FlatMember> members) {
        return members != null ? members.size() : 0;
    }
}