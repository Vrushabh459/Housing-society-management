package com.app.Mapper;

import java.util.List;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import com.app.dto.FlatMemberDTO;
import com.app.model.FlatMember;


@Mapper(componentModel = "spring")
public interface FlatMemberMapper {
    
    FlatMemberMapper INSTANCE = Mappers.getMapper(FlatMemberMapper.class);
    
    @Mapping(source = "flat.id", target = "flatId")
    @Mapping(source = "flat.flatNumber", target = "flatNumber")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "owner", target = "isOwner")
    FlatMemberDTO toDto(FlatMember flatMember);
    
    List<FlatMemberDTO> toDtoList(List<FlatMember> flatMembers);
    
    @Mapping(source = "flatId", target = "flat.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "owner", target = "isOwner")
    FlatMember toEntity(FlatMemberDTO flatMemberDto);
}
