package com.careercompass_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.ConsultancyResponseDTO;
import com.careercompass_backend.dto.GapCompanyResponseDTO;
import com.careercompass_backend.dto.ResourceResponseDTO;
import com.careercompass_backend.service.ResourceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResourceController {

	private final ResourceService resourceService;
	
	// GET /api/resources?city=Pune&type=CONSULTANCY
    // Both params optional — user can filter or get all
	@GetMapping("/resources")
	public ResponseEntity<List<ResourceResponseDTO>> getResources( @RequestParam(required=false) String city ,@RequestParam(required = false) String type){
		return ResponseEntity.ok(resourceService.getResources(city, type));
	}
	
    // GET /api/consultancies?city=Pune&fraudOnly=false
    // fraudOnly=false → safe consultancies
    // fraudOnly=true  → fraud warning list
    @GetMapping("/consultancies")
    public ResponseEntity<List<ConsultancyResponseDTO>> getConsultancies(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean fraudOnly) {
        return ResponseEntity.ok(
                resourceService.getConsultancies(city, fraudOnly));
    }

    // GET /api/gap-companies?gapMonths=6
    // Returns companies that accept this gap duration
    @GetMapping("/gap-companies")
    public ResponseEntity<List<GapCompanyResponseDTO>> getGapCompanies(
            @RequestParam(required = false) Integer gapMonths) {
        return ResponseEntity.ok(
                resourceService.getGapFriendlyCompanies(gapMonths));
    }
}
