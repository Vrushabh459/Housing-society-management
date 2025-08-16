package com.app.service;

import java.time.LocalDateTime;
import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.VisitorMapper;
import com.app.dao.FlatDao;
import com.app.dao.FlatMemberDao;
import com.app.dao.UserDao;
import com.app.dao.VisitorDao;
import com.app.dto.NotificationDto;
import com.app.dto.VisitorDTO;
import com.app.model.Flat;
import com.app.model.FlatMember;
import com.app.model.User;
import com.app.model.Visitor;
import com.app.model.VisitorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorDao visitorLogRepository;
    private final FlatDao flatRepository;
    private final UserDao userRepository;
    private final FlatMemberDao flatMemberRepository;
    private final VisitorMapper visitorLogMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> getAllVisitors() {
        List<Visitor> visitorLogs = visitorLogRepository.findAll();
        return visitorLogMapper.toDtoList(visitorLogs);
    }
    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> getVisitorLogsByFlatId(long flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException("Flat not found with id: " + flatId);
        }
        List<Visitor> visitorLogs = visitorLogRepository.findByvisitingFlatId(flatId);
        return visitorLogMapper.toDtoList(visitorLogs);
    }
    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> getVisitorLogsBySocietyId(Long societyId) {
        List<Visitor> visitorLogs = visitorLogRepository.findBySocietyId(societyId);
        return visitorLogMapper.toDtoList(visitorLogs);
    }
    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> getActiveVisitorsBySocietyId(Long societyId) {
        List<Visitor> visitorLogs = visitorLogRepository.findBySocietyIdAndExitTimeIsNull(societyId);
        return visitorLogMapper.toDtoList(visitorLogs);
    }
    @Override
    @Transactional(readOnly = true)
    public List<VisitorDTO> getPendingApprovalVisitorLogs(Long societyId) {
        List<Visitor> visitorLogs = visitorLogRepository.findBySocietyIdAndApproved(societyId, false);
        return visitorLogMapper.toDtoList(visitorLogs);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorDTO getVisitorById(long id) {
        Visitor visitorLog = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found with id: " + id));
        return visitorLogMapper.toDTO(visitorLog);
    }
    @Override
    @Transactional
    public VisitorDTO createVisitorLog(VisitorDTO visitorLogDto) {
        Flat flat = flatRepository.findById(visitorLogDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + visitorLogDto.getFlatId()));

        User loggedBy = userRepository.findById(visitorLogDto.getLoggedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + visitorLogDto.getLoggedById()));

        Visitor visitorLog = Visitor.builder()
                .name(visitorLogDto.getName())
                .phone(visitorLogDto.getPhone())
                .purpose(visitorLogDto.getPurpose())
                .visitingFlat(flat)
                .entryTime(LocalDateTime.now())
                .status(VisitorStatus.PENDING)
                .loggedBy(loggedBy)
                .build();

        Visitor savedVisitorLog = visitorLogRepository.save(visitorLog);
        VisitorDTO savedDto = visitorLogMapper.toDTO(savedVisitorLog);

        // Send notification to flat owners for approval
        List<FlatMember> owners = flatMemberRepository.findByFlatIdAndIsOwner(flat.getId(), true);
        for (FlatMember owner : owners) {
            if (owner.getUser() != null) {
                NotificationDto notification = NotificationDto.create(
                        "VISITOR_APPROVAL_REQUIRED",
                        "Visitor " + visitorLog.getName() + " is waiting for approval to visit flat " + flat.getFlatNumber(),
                        savedDto,
                        loggedBy.getId(),
                        loggedBy.getName(),
                        owner.getUser().getId(),
                        flat.getBuilding().getSociety().getId()
                );
                notificationService.sendPrivateNotification(notification);
            }
        }

        return savedDto;
    }
    @Override
    @Transactional
    public VisitorDTO approveVisitorLog(Long id, Long approverId) {
        Visitor visitorLog = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found with id: " + id));

        FlatMember approvedBy = flatMemberRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + approverId));

        visitorLog.setApproved(true);
        visitorLog.setApprovalTime(LocalDateTime.now());
        visitorLog.setApprovedBy(approvedBy);

        Visitor updatedVisitorLog = visitorLogRepository.save(visitorLog);
        VisitorDTO updatedDto = visitorLogMapper.toDTO(updatedVisitorLog);

        // Send notification to guards
        NotificationDto notification = NotificationDto.create(
                "VISITOR_APPROVED",
                "Visitor " + visitorLog.getName() + " has been approved to visit flat " + visitorLog.getVisitingFlat().getFlatNumber(),
                updatedDto,
                approverId,
                approvedBy.getName(),
                visitorLog.getLoggedBy().getId(),
                visitorLog.getVisitingFlat().getBuilding().getSociety().getId()
        );
        notificationService.sendGuardNotification(notification);

        return updatedDto;
    }
    @Override
    @Transactional
    public VisitorDTO recordVisitorExit(Long id) {
        Visitor visitorLog = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found with id: " + id));

        visitorLog.setExitTime(LocalDateTime.now());

        Visitor updatedVisitorLog = visitorLogRepository.save(visitorLog);
        return visitorLogMapper.toDTO(updatedVisitorLog);
    }
    @Override
    @Transactional
    public VisitorDTO updateVisitorLog(Long id, VisitorDTO visitorLogDto) {
        Visitor visitorLog = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found with id: " + id));

        Flat flat = flatRepository.findById(visitorLogDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + visitorLogDto.getFlatId()));

        User loggedBy = userRepository.findById(visitorLogDto.getLoggedById())
               .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + visitorLogDto.getLoggedById()));

        visitorLog.setName(visitorLogDto.getName());
        visitorLog.setPhone(visitorLogDto.getPhone());
        visitorLog.setPurpose(visitorLogDto.getPurpose());
        visitorLog.setVisitingFlat(flat);
        visitorLog.setLoggedBy(loggedBy);

        Visitor updatedVisitorLog = visitorLogRepository.save(visitorLog);
        return visitorLogMapper.toDTO(updatedVisitorLog);
    }

    @Override
    @Transactional
    public void deleteVisitor(long id) {
        if (!visitorLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("Visitor log not found with id: " + id);
        }
        visitorLogRepository.deleteById(id);
    }


}
