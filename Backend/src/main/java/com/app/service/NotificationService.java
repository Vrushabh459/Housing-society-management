package com.app.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.app.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendPrivateNotification(NotificationDto notification) {
        if (notification.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    notification.getRecipientId().toString(),
                    "/queue/notifications",
                    notification
            );
            log.debug("Sent private notification to user: {}", notification.getRecipientId());
        }
    }

    public void sendSocietyNotification(NotificationDto notification) {
        if (notification.getSocietyId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/society/" + notification.getSocietyId(),
                    notification
            );
            log.debug("Sent society notification to society: {}", notification.getSocietyId());
        }
    }

    public void sendAdminNotification(NotificationDto notification) {
        if (notification.getSocietyId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/admin/" + notification.getSocietyId(),
                    notification
            );
            log.debug("Sent admin notification to society: {}", notification.getSocietyId());
        }
    }

    public void sendResidentNotification(NotificationDto notification) {
        if (notification.getSocietyId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/resident/" + notification.getSocietyId(),
                    notification
            );
            log.debug("Sent resident notification to society: {}", notification.getSocietyId());
        }
    }

    public void sendGuardNotification(NotificationDto notification) {
        if (notification.getSocietyId() != null) {
            messagingTemplate.convertAndSend(
                    "/topic/guard/" + notification.getSocietyId(),
                    notification
            );
            log.debug("Sent guard notification to society: {}", notification.getSocietyId());
        }
    }

    public void sendGlobalNotification(NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/global", notification);
        log.debug("Sent global notification");
    }
}