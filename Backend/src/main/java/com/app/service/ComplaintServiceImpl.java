package com.app.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.ComplaintMapper;
import com.app.dao.ComplaintDao;
import com.app.dao.FlatDao;
import com.app.dao.FlatMemberDao;
import com.app.dao.UserDao;
import com.app.dto.ComplaintDTO;
import com.app.dto.NotificationDto;
import com.app.model.Complaint;
import com.app.model.ComplaintStatus;
import com.app.model.Flat;
import com.app.model.FlatMember;
import com.app.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintDao complaintRepository;
    private final FlatDao flatRepository;
    private final FlatMemberDao flatmemberRepo;
    private final UserDao userRepository;
    private final ComplaintMapper complaintMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAll();
        return complaintMapper.toDtoList(complaints);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getComplaintsByFlatId(Long flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException("Flat not found with id: " + flatId);
        }
        List<Complaint> complaints = complaintRepository.findByFlatId(flatId);
        return complaintMapper.toDtoList(complaints);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getComplaintsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<Complaint> complaints = complaintRepository.findByCreatedById(userId);
        return complaintMapper.toDtoList(complaints);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getComplaintsBySocietyId(Long societyId) {
        List<Complaint> complaints = complaintRepository.findBySocietyId(societyId);
        return complaintMapper.toDtoList(complaints);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDTO> getComplaintsBySocietyIdAndStatus(Long societyId, ComplaintStatus status) {
        List<Complaint> complaints = complaintRepository.findBySocietyIdAndStatus(societyId, status);
        return complaintMapper.toDtoList(complaints);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ComplaintDTO getComplaintById(long id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));
        return complaintMapper.toDTO(complaint);
    }

    @Override
    @Transactional
    public ComplaintDTO createComplaint(ComplaintDTO complaintDto) {
        Flat flat = flatRepository.findById(complaintDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + complaintDto.getFlatId()));

        FlatMember raisedBy = flatmemberRepo.findById(complaintDto.getRaisedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + complaintDto.getRaisedById()));

        Complaint complaint = Complaint.builder()
                .title(complaintDto.getTitle())
                .description(complaintDto.getDescription())
                .status(ComplaintStatus.PENDING)
                .flat(flat)
                .createdBy(raisedBy)
                .build();

        Complaint savedComplaint = complaintRepository.save(complaint);
        ComplaintDTO savedDto = complaintMapper.toDTO(savedComplaint);

        // Send notification to admins
        NotificationDto notification = NotificationDto.create(
                "NEW_COMPLAINT",
                "New complaint: " + complaint.getTitle(),
                savedDto,
                raisedBy.getId(),
                raisedBy.getName(),
                null,
                flat.getBuilding().getSociety().getId()
        );
        notificationService.sendAdminNotification(notification);

        return savedDto;
    }
    @Override
    @Transactional
    public ComplaintDTO updateComplaintStatus(Long id, ComplaintStatus status, String resolution, Long adminUserId) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        complaint.setStatus(status);
        
        if (status == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
            complaint.setResolution(resolution);
        }

        Complaint updatedComplaint = complaintRepository.save(complaint);
        ComplaintDTO updatedDto = complaintMapper.toDTO(updatedComplaint);

        // Get admin name
        String adminName = "Admin";
        if (adminUserId != null) {
            User admin = userRepository.findById(adminUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + adminUserId));
            adminName = admin.getName();
        }

        // Send notification to the user who raised the complaint
        NotificationDto notification = NotificationDto.create(
                "COMPLAINT_STATUS_UPDATED",
                "Your complaint status has been updated to " + status,
                updatedDto,
                adminUserId,
                adminName,
                complaint.getCreatedBy().getId(),
                complaint.getFlat().getBuilding().getSociety().getId()
        );
        notificationService.sendPrivateNotification(notification);

        return updatedDto;
    }
    @Override
    @Transactional
    public ComplaintDTO updateComplaint(Long id, ComplaintDTO complaintDto) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + id));

        Flat flat = flatRepository.findById(complaintDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + complaintDto.getFlatId()));

        FlatMember raisedBy = flatmemberRepo.findById(complaintDto.getRaisedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + complaintDto.getRaisedById()));

        complaint.setTitle(complaintDto.getTitle());
        complaint.setDescription(complaintDto.getDescription());
        complaint.setFlat(flat);
        complaint.setCreatedBy(raisedBy);

        Complaint updatedComplaint = complaintRepository.save(complaint);
        return complaintMapper.toDTO(updatedComplaint);
    }
    
    @Override
    @Transactional
    public void deleteComplaint(long id) {
        if (!complaintRepository.existsById(id)) {
            throw new ResourceNotFoundException("Complaint not found with id: " + id);
        }
        complaintRepository.deleteById(id);
    }

}
