package com.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.NoticeMapper;
import com.app.dao.NoticeDao;
import com.app.dao.SocietyDao;
import com.app.dao.UserDao;
import com.app.dto.NoticeDTO;
import com.app.dto.NotificationDto;
import com.app.model.Notice;
import com.app.model.Society;
import com.app.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeDao noticeRepository;
    private final SocietyDao societyRepository;
    private final UserDao userRepository;
    private final NoticeMapper noticeMapper;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<NoticeDTO> getAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return noticeMapper.toDtoList(notices);
    }

    @Transactional(readOnly = true)
    public List<NoticeDTO> getNoticesBySocietyId(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }
        List<Notice> notices = noticeRepository.findBySocietyId(societyId);
        return noticeMapper.toDtoList(notices);
    }

    @Transactional(readOnly = true)
    public List<NoticeDTO> getActiveNoticesBySocietyId(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }
        List<Notice> notices = noticeRepository.findBySocietyIdAndIsActive(societyId, true);
        return noticeMapper.toDtoList(notices);
    }

    @Transactional(readOnly = true)
    public NoticeDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));
        return noticeMapper.toDTO(notice);
    }

    @Transactional
    public NoticeDTO createNotice(NoticeDTO noticeDto) {
        Society society = societyRepository.findById(noticeDto.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + noticeDto.getSocietyId()));

        User createdBy = userRepository.findById(noticeDto.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + noticeDto.getCreatedById()));

        Notice notice = Notice.builder()
                .title(noticeDto.getTitle())
                .content(noticeDto.getContent())
                .society(society)
                .createdBy(createdBy)
                .expiresAt(noticeDto.getExpiryDate())
                .isActive(true)
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        NoticeDTO savedDto = noticeMapper.toDTO(savedNotice);

        // Send notification to all society members
        NotificationDto notification = NotificationDto.create(
                "NEW_NOTICE",
                "New notice: " + notice.getTitle(),
                savedDto,
                createdBy.getId(),
                createdBy.getName(),
                null,
                society.getId()
        );
        notificationService.sendSocietyNotification(notification);

        return savedDto;
    }

    @Transactional
    public NoticeDTO updateNotice(Long id, NoticeDTO noticeDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));

        Society society = societyRepository.findById(noticeDto.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + noticeDto.getSocietyId()));

        User createdBy = userRepository.findById(noticeDto.getCreatedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + noticeDto.getCreatedById()));

        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());
        notice.setSociety(society);
        notice.setCreatedBy(createdBy);
        notice.setExpiresAt(noticeDto.getExpiryDate());
        notice.setIsActive(noticeDto.isActive());

        Notice updatedNotice = noticeRepository.save(notice);
        NoticeDTO updatedDto = noticeMapper.toDTO(updatedNotice);

        // Send notification about updated notice
        if (notice.getIsActive()) {
            NotificationDto notification = NotificationDto.create(
                    "NOTICE_UPDATED",
                    "Notice updated: " + notice.getTitle(),
                    updatedDto,
                    createdBy.getId(),
                    createdBy.getName(),
                    null,
                    society.getId()
            );
            notificationService.sendSocietyNotification(notification);
        }

        return updatedDto;
    }

    @Transactional
    public NoticeDTO deactivateNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice not found with id: " + id));

        notice.setIsActive(false);
        Notice updatedNotice = noticeRepository.save(notice);
        return noticeMapper.toDTO(updatedNotice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notice not found with id: " + id);
        }
        noticeRepository.deleteById(id);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void deactivateExpiredNotices() {
        List<Notice> expiredNotices = noticeRepository.findByExpiresAtBeforeAndIsActive(LocalDateTime.now(), true);
        for (Notice notice : expiredNotices) {
            notice.setIsActive(false);
            noticeRepository.save(notice);
        }
    }
}
