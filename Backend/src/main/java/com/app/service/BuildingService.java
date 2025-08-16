package com.app.service;

import java.util.List;

import com.app.dto.BuildingDTO;


public interface BuildingService {
	
	public List<BuildingDTO> getAllBuildings();
	
	public BuildingDTO createBuilding(BuildingDTO building);
	
	public BuildingDTO getBuildingById(Long id);
	
	public void deleteBuilding(Long id);
	
	public List<BuildingDTO> getBuildingsBySocietyId(Long societyId);
	
	public BuildingDTO updateBuilding(Long id, BuildingDTO buildingDto);
}
