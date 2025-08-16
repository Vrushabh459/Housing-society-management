package com.app.service;

import java.util.List;

import com.app.dto.VisitorDTO;


public interface VisitorService {
	
public List<VisitorDTO> getAllVisitors();
	
	public VisitorDTO createVisitorLog(VisitorDTO visit);
	
	public VisitorDTO getVisitorById(long id);
	
	public void deleteVisitor(long id);

	List<VisitorDTO> getVisitorLogsByFlatId(long flatId);

	List<VisitorDTO> getVisitorLogsBySocietyId(Long societyId);

	List<VisitorDTO> getActiveVisitorsBySocietyId(Long societyId);

	List<VisitorDTO> getPendingApprovalVisitorLogs(Long societyId);

	VisitorDTO approveVisitorLog(Long id, Long approverId);

	VisitorDTO recordVisitorExit(Long id);

	VisitorDTO updateVisitorLog(Long id, VisitorDTO visitorLogDto);
}
