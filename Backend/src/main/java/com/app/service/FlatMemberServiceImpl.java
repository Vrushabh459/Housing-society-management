package com.app.service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.FlatMemberMapper;
import com.app.dao.FlatDao;
import com.app.dao.FlatMemberDao;
import com.app.dao.UserDao;
import com.app.dto.FlatMemberDTO;
import com.app.dto.NotificationDto;
import com.app.model.Flat;
import com.app.model.FlatMember;
import com.app.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlatMemberServiceImpl implements FlatMemberService {

    private final FlatMemberDao flatMemberRepository;
    private final FlatDao flatRepository;
    private final UserDao userRepository;
    private final FlatMemberMapper flatMemberMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public List<FlatMemberDTO> getAllFlatMembers() {
        List<FlatMember> flatMembers = flatMemberRepository.findAll();
        return flatMemberMapper.toDtoList(flatMembers);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlatMemberDTO> getFlatMembersByFlatId(Long flatId) {
        if (!flatRepository.existsById(flatId)) {
            throw new ResourceNotFoundException("Flat not found with id: " + flatId);
        }
        List<FlatMember> flatMembers = flatMemberRepository.findByFlatId(flatId);
        return flatMemberMapper.toDtoList(flatMembers);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlatMemberDTO> getFlatMembersByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        List<FlatMember> flatMembers = flatMemberRepository.findByUserId(userId);
        return flatMemberMapper.toDtoList(flatMembers);
    }
    @Override
    @Transactional(readOnly = true)
    public List<FlatMemberDTO> getPendingFlatMembersBySocietyId(Long societyId) {
        List<FlatMember> pendingMembers = flatMemberRepository.findBySocietyIdAndApproved(societyId, false);
        return flatMemberMapper.toDtoList(pendingMembers);
    }

    @Override
    @Transactional(readOnly = true)
    public FlatMemberDTO getFlatMemberById(Long id) {
        FlatMember flatMember = flatMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat member not found with id: " + id));
        return flatMemberMapper.toDto(flatMember);
    }
    @Override
    @Transactional
    public FlatMemberDTO createFlatMember(FlatMemberDTO flatMemberDto, Long currentUserId) {
        Flat flat = flatRepository.findById(flatMemberDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + flatMemberDto.getFlatId()));

        User user = null;
        if (flatMemberDto.getUserId() != null) {
            user = userRepository.findById(flatMemberDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + flatMemberDto.getUserId()));
        }

        // Check if this is the first member (owner) or if the current user is already an owner
        boolean isFirstMember = flatMemberRepository.findByFlatId(flat.getId()).isEmpty();
        boolean isCurrentUserOwner = false;
        
        if (!isFirstMember && currentUserId != null) {
            isCurrentUserOwner = flatMemberRepository.findByFlatIdAndIsOwner(flat.getId(), true)
                    .stream()
                    .anyMatch(member -> member.getUser() != null && member.getUser().getId().equals(currentUserId));
        }

        // Only the first member or an existing owner can add new members
        @SuppressWarnings("unused")
		boolean canAddMember = isFirstMember || isCurrentUserOwner;
        
        // First member is automatically approved, others need admin approval
        boolean isApproved = isFirstMember;

        FlatMember flatMember = FlatMember.builder()
                .name(flatMemberDto.getName())
                .phone(flatMemberDto.getPhone())
                .email(flatMemberDto.getEmail())
                .relationship(flatMemberDto.getRelationship())
                .isOwner(flatMemberDto.isOwner())
                .flat(flat)
                .user(user)
                .approved(isApproved)
                .build();

        FlatMember savedFlatMember = flatMemberRepository.save(flatMember);
        FlatMemberDTO savedDto = flatMemberMapper.toDto(savedFlatMember);

        // Send notification to admins if this is not the first member
        if (!isFirstMember) {
            NotificationDto notification = NotificationDto.create(
                    "NEW_FLAT_MEMBER_REQUEST",
                    "New flat member request for " + flat.getFlatNumber(),
                    savedDto,
                    currentUserId,
                    flatMemberDto.getName(),
                    null,
                    flat.getBuilding().getSociety().getId()
            );
            notificationService.sendAdminNotification(notification);
        }

        return savedDto;
    }
    @Override
    @Transactional
    public FlatMemberDTO updateFlatMember(Long id, FlatMemberDTO flatMemberDto) {
        FlatMember flatMember = flatMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat member not found with id: " + id));

        Flat flat = flatRepository.findById(flatMemberDto.getFlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Flat not found with id: " + flatMemberDto.getFlatId()));

        User user = null;
        if (flatMemberDto.getUserId() != null) {
            user = userRepository.findById(flatMemberDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + flatMemberDto.getUserId()));
        }

        flatMember.setName(flatMemberDto.getName());
        flatMember.setPhone(flatMemberDto.getPhone());
        flatMember.setEmail(flatMemberDto.getEmail());
        flatMember.setRelationship(flatMemberDto.getRelationship());
        flatMember.setOwner(flatMemberDto.isOwner());
        flatMember.setFlat(flat);
        flatMember.setUser(user);

        FlatMember updatedFlatMember = flatMemberRepository.save(flatMember);
        return flatMemberMapper.toDto(updatedFlatMember);
    }
    
    @Override
    @Transactional
    public FlatMemberDTO approveFlatMember(Long id, Long adminUserId) {
        FlatMember flatMember = flatMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flat member not found with id: " + id));

        flatMember.setApproved(true);
        FlatMember updatedFlatMember = flatMemberRepository.save(flatMember);
        FlatMemberDTO updatedDto = flatMemberMapper.toDto(updatedFlatMember);

        // Get admin name
        String adminName = "Admin";
        Optional<User> adminUser = userRepository.findById(adminUserId);
        if (adminUser.isPresent()) {
            adminName = adminUser.get().getName();
        }

        // Send notification to the user if they have a user account
        if (flatMember.getUser() != null) {
            NotificationDto notification = NotificationDto.create(
                    "FLAT_MEMBER_APPROVED",
                    "Your flat membership has been approved",
                    updatedDto,
                    adminUserId,
                    adminName,
                    flatMember.getUser().getId(),
                    flatMember.getFlat().getBuilding().getSociety().getId()
            );
            notificationService.sendPrivateNotification(notification);
        }

        return updatedDto;
    }

    @Override
    @Transactional
    public void deleteFlatMember(Long id) {
        if (!flatMemberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flat member not found with id: " + id);
        }
        flatMemberRepository.deleteById(id);
    }

	
}
