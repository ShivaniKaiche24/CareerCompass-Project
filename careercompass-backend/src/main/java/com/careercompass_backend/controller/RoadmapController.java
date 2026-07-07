package com.careercompass_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.RoadmapResponseDTO;
import com.careercompass_backend.service.RoadmapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

	private final RoadmapService roadmapService;
	
	/*
     * POST /api/roadmap/generate?userId=1
     *
     * Why userId as a request param and not from the JWT?
     * On Day 11 (cleanup day) we will extract it from SecurityContext.
     * For now, accepting it explicitly is simpler and works fine for testing.
     *
     * 201 CREATED — we are creating a new Roadmap resource.
     * This call takes 1-3 seconds because of the Gemini API call.
     * In production you would make this async (@Async) and return
     * a job ID to poll — but synchronous is fine for this project.
     */
	
	@PostMapping("/generate")
	public ResponseEntity<RoadmapResponseDTO> generateRoadmap (
			@RequestParam Long userId){
		RoadmapResponseDTO response = roadmapService.generateRoadmap(userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	  /*
     * GET /api/roadmap/active?userId=1
     * Returns the user's current active roadmap — no AI call, just DB lookup.
     */
	
	@GetMapping("/active")
	public ResponseEntity<RoadmapResponseDTO> getActiveRoadmap (
			@RequestParam Long userId){
		RoadmapResponseDTO response = roadmapService.getActiveRoadmap(userId);
		return ResponseEntity.ok(response);
	}
}
