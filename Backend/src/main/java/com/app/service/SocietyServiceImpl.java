package com.app.service;

import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Exception.ResourceAlreadyExistsException;
import com.app.Exception.ResourceNotFoundException;
import com.app.Mapper.SocietyMapper;
import com.app.dao.SocietyDao;
import com.app.dto.SocietyCreationRequest;
import com.app.dto.SocietyDTO;

import com.app.model.Society;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocietyServiceImpl implements SocietyService {

	
    private final SocietyDao societyRepository;
	
    private final SocietyMapper societyMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SocietyDTO> getAllSocieties() {
        List<Society> societies = societyRepository.findAll();
        return societyMapper.toDtoList(societies);
    }

    @Override
    @Transactional(readOnly = true)
    public SocietyDTO getSocietyById(Long id) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + id));
        return societyMapper.toDTO(society);
    }

    @Override
    @Transactional
    public Society createSociety(SocietyCreationRequest request) {
        // Check if society with same name already exists
        if (societyRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Society already exists with name: " + request.getName());
        }

        Society society = Society.builder()
                .name(request.getName())
                .address(request.getAddress())
                .numberOfBuildings(request.getNumberOfBuildings())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .build();

        return societyRepository.save(society);
    }
    
    @Override
    @Transactional
    public SocietyDTO updateSociety(Long id, SocietyDTO societyDto) {
        Society society = societyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Society not found with id: " + id));

        // Check if name is being changed and if it already exists
        if (!society.getName().equals(societyDto.getName()) && 
                societyRepository.existsByName(societyDto.getName())) {
            throw new ResourceAlreadyExistsException("Society already exists with name: " + societyDto.getName());
        }

        society.setName(societyDto.getName());
        society.setAddress(societyDto.getAddress());
        society.setCity(societyDto.getCity());
        society.setState(societyDto.getState());
        society.setPincode(societyDto.getPincode());
        society.setNumberOfBuildings(societyDto.getNumberOfBuildings());

        Society updatedSociety = societyRepository.save(society);
        return societyMapper.toDTO(updatedSociety);
    }

    @Override
    @Transactional
    public void deleteSociety(Long id) {
        if (!societyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Society not found with id: " + id);
        }
        societyRepository.deleteById(id);
    }

	

	
}

