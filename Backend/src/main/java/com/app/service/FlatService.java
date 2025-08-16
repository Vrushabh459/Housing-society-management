package com.app.service;

import java.util.List;

import com.app.dto.FlatDTO;



public interface FlatService {
	
public List<FlatDTO> getAllFlats();
	
	public FlatDTO createFlat(FlatDTO flat);
	
	public FlatDTO getFlatById(Long id);
	
	public void deleteFlat(Long id);
	
	public List<FlatDTO> getFlatsByBuildingId(Long buildingId);

	List<FlatDTO> getFlatsBySocietyId(Long societyId);

	FlatDTO updateFlat(Long id, FlatDTO flatDto);
	
	FlatDTO getFlatByUserId(Long userId);
}
