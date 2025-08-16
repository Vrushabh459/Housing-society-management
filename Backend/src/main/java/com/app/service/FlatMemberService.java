package com.app.service;

import java.util.List;

import com.app.dto.FlatMemberDTO;


public interface FlatMemberService {
	
	public List<FlatMemberDTO> getAllFlatMembers();
	
	public FlatMemberDTO createFlatMember(FlatMemberDTO member,Long id);
	
	public FlatMemberDTO getFlatMemberById(Long id);
	
	public void deleteFlatMember(Long id);


	List<FlatMemberDTO> getPendingFlatMembersBySocietyId(Long societyId);

	List<FlatMemberDTO> getFlatMembersByUserId(Long userId);

	List<FlatMemberDTO> getFlatMembersByFlatId(Long flatId);

	FlatMemberDTO updateFlatMember(Long id, FlatMemberDTO flatMemberDto);

	FlatMemberDTO approveFlatMember(Long id, Long adminUserId);
}
