package com.app.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.app.dto.NoticeDTO;
import com.app.model.Notice;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    
   // NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);
    
    @Mapping(source = "society.id", target = "societyId")
    @Mapping(source = "society.name", target = "societyName")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.name", target = "createdByName")
    @Mapping(source = "expiresAt", target = "expiryDate")
    @Mapping(source = "isActive", target = "active")
    
    NoticeDTO toDTO(Notice notice);
    
    List<NoticeDTO> toDtoList(List<Notice> notices);
    
    @Mapping(source = "societyId", target = "society.id")
    @Mapping(source = "createdById", target = "createdBy.id")
    @Mapping(source = "expiryDate", target = "expiresAt")
    @Mapping(source = "active", target = "isActive")
    Notice toEntity(NoticeDTO noticeDto);
}

