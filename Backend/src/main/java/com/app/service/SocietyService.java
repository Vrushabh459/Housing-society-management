package com.app.service;

import java.util.List;

import com.app.dto.SocietyCreationRequest;
import com.app.dto.SocietyDTO;
import com.app.model.Society;


public interface SocietyService {
	
public List<SocietyDTO> getAllSocieties();
	
	public Society createSociety(SocietyCreationRequest society);
	
	public SocietyDTO getSocietyById(Long id);
	
	public SocietyDTO updateSociety(Long id, SocietyDTO societyDto);
	
	public void deleteSociety(Long id);
}
