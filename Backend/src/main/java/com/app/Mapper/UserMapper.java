package com.app.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.app.dto.UserDTO;
import com.app.dto.UserRegistrationDTO;
import com.app.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(source = "user", target = "societyId", qualifiedByName = "getSocietyId")
    @Mapping(source = "user", target = "societyName", qualifiedByName = "getSocietyName")
    UserDTO toDTO(User user);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(UserRegistrationDTO dto);
    
    @Named("getSocietyId")
    default Long getSocietyId(User user) {
        return user != null && user.getSociety() != null ? user.getSociety().getId() : null;
    }
    
    @Named("getSocietyName")
    default String getSocietyName(User user) {
        return user != null && user.getSociety() != null ? user.getSociety().getName() : null;
    }
    
}
