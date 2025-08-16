package com.app.service;

import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceAlreadyExistsException;
import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.BuildingMapper;
import com.app.dao.BuildingDao;
import com.app.dao.SocietyDao;
import com.app.dto.BuildingDTO;
import com.app.model.Building;
import com.app.model.Society;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingDao buildingRepository;
    private final SocietyDao societyRepository;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        return buildingMapper.toDtoList(buildings);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BuildingDTO> getBuildingsBySocietyId(Long societyId) {
        if (!societyRepository.existsById(societyId)) {
            throw new ResourceNotFoundException("Society not found with id: " + societyId);
        }
        List<Building> buildings = buildingRepository.findBySocietyId(societyId);
        return buildingMapper.toDtoList(buildings);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingDTO getBuildingById(Long id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));
        return buildingMapper.toDTO(building);
    }

    @Override
    @Transactional
    public BuildingDTO createBuilding(BuildingDTO buildingDto) {
        Society society = societyRepository.findById(buildingDto.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + buildingDto.getSocietyId()));

        // Check if building with same name already exists in the society
        if (buildingRepository.existsByNameAndSocietyId(buildingDto.getName(), buildingDto.getSocietyId())) {
            throw new ResourceAlreadyExistsException("Building already exists with name: " + buildingDto.getName() + " in society: " + society.getName());
        }

        Building building = Building.builder()
                .name(buildingDto.getName())
                .society(society)
                .build();

        Building savedBuilding = buildingRepository.save(building);
        return buildingMapper.toDTO(savedBuilding);
    }
    @Override
    @Transactional
    public BuildingDTO updateBuilding(Long id, BuildingDTO buildingDto) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        Society society = societyRepository.findById(buildingDto.getSocietyId())
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + buildingDto.getSocietyId()));

        // Check if name is being changed and if it already exists in the society
        if (!building.getName().equals(buildingDto.getName()) && 
                buildingRepository.existsByNameAndSocietyId(buildingDto.getName(), buildingDto.getSocietyId())) {
            throw new ResourceAlreadyExistsException("Building already exists with name: " + buildingDto.getName() + " in society: " + society.getName());
        }

        building.setName(buildingDto.getName());
        building.setSociety(society);

        Building updatedBuilding = buildingRepository.save(building);
        return buildingMapper.toDTO(updatedBuilding);
    }
    
    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Building not found with id: " + id);
        }
        buildingRepository.deleteById(id);
    }

}
