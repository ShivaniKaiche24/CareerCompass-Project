package com.careercompass_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.ApplicationRequestDTO;
import com.careercompass_backend.dto.ApplicationResponseDTO;
import com.careercompass_backend.service.ApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

	private final ApplicationService applicationService;
	
	// POST /api/applications?userId=1
    // userId from query param, application details from body
	
	@PostMapping
	public ResponseEntity<ApplicationResponseDTO> logApplication(@RequestParam Long userId , @RequestBody ApplicationRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.logApplication(userId, request));
	}
	
	@GetMapping
	public ResponseEntity<List<ApplicationResponseDTO>> getAllApplication(@RequestParam Long userId) {
		return ResponseEntity.ok(applicationService.getAllApplication(userId));
	}
	// PUT /api/applications/5/status?userId=1&newStatus=INTERVIEW_SCHEDULED
    // applicationId from URL path
    // newStatus from query param — keeps body empty
	@PutMapping("/{applicationId}/status")
	public ResponseEntity<ApplicationResponseDTO> updateStatus(
			@PathVariable Long applicationId ,
			@RequestParam Long userId , 
			@RequestParam String newStatus){
		
		return ResponseEntity.ok(applicationService.updateStatus(applicationId, userId, newStatus));
	}
	
	 // GET /api/applications/followups?userId=1
	@GetMapping("/followups")
	public ResponseEntity<List<ApplicationResponseDTO>> getFollowUps(@RequestParam Long userId) {
		return ResponseEntity.ok(applicationService.getFollowUpsForToday(userId));
	}
}
