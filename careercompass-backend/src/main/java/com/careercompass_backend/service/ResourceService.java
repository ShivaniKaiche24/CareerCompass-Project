package com.careercompass_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.careercompass_backend.dto.ConsultancyResponseDTO;
import com.careercompass_backend.dto.GapCompanyResponseDTO;
import com.careercompass_backend.dto.ResourceResponseDTO;
import com.careercompass_backend.model.GapFriendlyCompany;
import com.careercompass_backend.model.Resource;
import com.careercompass_backend.repository.ConsultancyRepository;
import com.careercompass_backend.repository.GapFriendlyCompanyRepository;
import com.careercompass_backend.repository.ResourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

	private final ResourceRepository resourceRepository;
	private final ConsultancyRepository consultancyRepository;
	private final GapFriendlyCompanyRepository gapFriendlyCompanyRepository;
	/*
	 * getResources()
	 * If both provided — call findByCityAndType(city, type)
     * If only type — call findByType(type)
     * If only city — call findByCity(city)
     * If neither — call findByIsGenuine(true) — return all genuine resources
	 */
	
	public List<ResourceResponseDTO> getResources(String city , String type){
		
		List<Resource> resources ;
		if(city != null && type != null) {
			resources = resourceRepository.findByCityAndType(city, type);
		}else if(type != null) {
			resources = resourceRepository.findByType(type);
		}else if(city != null) {
			resources= resourceRepository.findByCity(city);
		}else {
			resources= resourceRepository.findByIsGenuine(true);
		}
		
		return resources.stream().map(this::toDTO).collect(Collectors.toList());
		
		
	}
	
	
	 // ─────────────────────────────────────────────────
    // GET CONSULTANCIES
    //
    // Why show fraud ones too?
    // Gap candidates need to know which consultancies to AVOID.
    // Showing fraud-flagged ones with a warning is more useful
    // than hiding them completely.
    //
    // fraudOnly=true → returns the warning list
    // fraudOnly=false → returns safe consultancies
    // ─────────────────────────────────────────────────
    public List<ConsultancyResponseDTO> getConsultancies(
            String city, Boolean fraudOnly) {

        // Default to showing safe consultancies
        boolean showFraud = fraudOnly != null && fraudOnly;

        List<com.careercompass_backend.model.Consultancy> consultancies;

        if (city != null) {
            consultancies = consultancyRepository
                    .findByCityAndIsFraud(city, showFraud);
        } else {
            consultancies = consultancyRepository
                    .findByIsFraud(showFraud);
        }

        return consultancies.stream()
                .map(this::toConsultancyDTO)
                .collect(Collectors.toList());
    }
	
	

    // ─────────────────────────────────────────────────
    // GET GAP-FRIENDLY COMPANIES
    //
    // Why filter by gapMonths?
    // A company accepting max 6 months gap is useless
    // for someone with 18 months gap.
    // This filter returns only companies where
    // maxGapMonths >= user's actual gap.
    // ─────────────────────────────────────────────────
    public List<GapCompanyResponseDTO> getGapFriendlyCompanies(
            Integer gapMonths) {

        List<GapFriendlyCompany> companies;

        if (gapMonths != null) {
            companies = gapFriendlyCompanyRepository
                    .findByMaxGapMonthsGreaterThanEqual(gapMonths);
        } else {
            // No filter — return all active companies
            companies = gapFriendlyCompanyRepository
                    .findByScreeningTypeAndIsActive(
                            "DIRECT_HR", true);
        }

        return companies.stream()
                .map(this::toGapCompanyDTO)
                .collect(Collectors.toList());
    }
	
	
	
	
	
	
	
	
	
	/*
	 * DTO ---> toDTO method
	 */
	
	public ResourceResponseDTO toDTO (Resource resource) {
		ResourceResponseDTO dto = new ResourceResponseDTO();
		
		dto.setResourceId(resource.getResourceId());
		dto.setCity(resource.getCity());
		dto.setFeeRange(resource.getFeeRange());
		dto.setIsGenuine(resource.getIsGenuine());
		dto.setName(resource.getName());
		dto.setNotes(resource.getNotes());
		dto.setType(resource.getType());
		dto.setUrl(resource.getUrl());
		
		return dto;
		
	}
	
	private ConsultancyResponseDTO toConsultancyDTO(
            com.careercompass_backend.model.Consultancy c) {
        ConsultancyResponseDTO dto = new ConsultancyResponseDTO();
        
        dto.setConsultantId(c.getCounstantId());
        dto.setName(c.getName());
        dto.setCity(c.getCity());
        dto.setPhone(c.getPhone());
        dto.setIsFraud(c.getIsFraud());
        dto.setReviewNotes(c.getReviewNotes());
        return dto;
    }
	  private GapCompanyResponseDTO toGapCompanyDTO(
	            GapFriendlyCompany g) {
	        GapCompanyResponseDTO dto = new GapCompanyResponseDTO();
	        dto.setCompanyId(g.getCompanyId());
	        dto.setCompanyName(g.getCompanyName());
	        dto.setCompanyType(g.getCompanyType());
	        dto.setCity(g.getCity());
	        dto.setMaxGapMonths(g.getMaxGapMonths());
	        dto.setScreeningType(g.getScreeningType());
	        dto.setCdacRecognized(g.getCdacRecognized());
	        dto.setMinPackageLpa(g.getMinPackageLpa());
	        dto.setMaxPackageLpa(g.getMaxPackageLpa());
	        dto.setApplyVia(g.getApplyVia());
	        dto.setNotes(g.getNotes());
	        return dto;
	    }
}
