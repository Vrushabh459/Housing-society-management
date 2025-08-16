package com.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String type;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
    private Long senderId;
    private String senderName;
    private Long recipientId;
    private Long societyId;
    
    public static NotificationDto create(String type, String message, Object data, Long senderId, String senderName, Long recipientId, Long societyId) {
        return NotificationDto.builder()
                .type(type)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .senderId(senderId)
                .senderName(senderName)
                .recipientId(recipientId)
                .societyId(societyId)
                .build();
    }
}
