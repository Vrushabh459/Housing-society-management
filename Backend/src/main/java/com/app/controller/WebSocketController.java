package com.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.app.dto.NotificationDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/private-notification")
    public void sendPrivateNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
                notification.getRecipientId().toString(),
                "/queue/notifications",
                notification
        );
    }

    @MessageMapping("/society-notification")
    public void sendSocietyNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSend(
                "/topic/society/" + notification.getSocietyId(),
                notification
        );
    }

    @MessageMapping("/admin-notification")
    public void sendAdminNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSend(
                "/topic/admin/" + notification.getSocietyId(),
                notification
        );
    }

    @MessageMapping("/resident-notification")
    public void sendResidentNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSend(
                "/topic/resident/" + notification.getSocietyId(),
                notification
        );
    }

    @MessageMapping("/guard-notification")
    public void sendGuardNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSend(
                "/topic/guard/" + notification.getSocietyId(),
                notification
        );
    }

    @MessageMapping("/global-notification")
    public void sendGlobalNotification(@Payload NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/global", notification);
    }
}