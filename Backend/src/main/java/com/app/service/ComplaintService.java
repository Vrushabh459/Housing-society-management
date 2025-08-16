package com.app.service;

import java.util.List;

import com.app.dto.ComplaintDTO;
import com.app.model.ComplaintStatus;


public interface ComplaintService {
	
public List<ComplaintDTO> getAllComplaints();
	
	public ComplaintDTO createComplaint(ComplaintDTO complaint);
	
	public ComplaintDTO getComplaintById(long id);
	
	public void deleteComplaint(long id);

	List<ComplaintDTO> getComplaintsByFlatId(Long flatId);

	List<ComplaintDTO> getComplaintsByUserId(Long userId);

	List<ComplaintDTO> getComplaintsBySocietyId(Long societyId);

	List<ComplaintDTO> getComplaintsBySocietyIdAndStatus(Long societyId, ComplaintStatus status);

	ComplaintDTO updateComplaintStatus(Long id, ComplaintStatus status, String resolution, Long adminUserId);

	ComplaintDTO updateComplaint(Long id, ComplaintDTO complaintDto);
}
