package com.careercompass_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.ResumeOptimizerRequestDTO;
import com.careercompass_backend.dto.ResumeOptimizerResponseDTO;
import com.careercompass_backend.service.ResumeOptimizerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeOptimizerController {

	private final ResumeOptimizerService resumeOptimizerService;
	
	/*
     * POST /api/resume/optimize?userId=1
     * User pastes a job description in the body.
     * Gemini analyzes it against their profile.
     * Returns keywords to add, matching skills,
     * missing skills, and a match score.
     *
     * This feature was Shivani's own idea —
     * not from any tutorial.
     */
	@PostMapping("/optimize")
    public ResponseEntity<ResumeOptimizerResponseDTO> optimizeResume(
            @RequestParam Long userId,
            @RequestBody ResumeOptimizerRequestDTO request) {
        return ResponseEntity.ok(
                resumeOptimizerService.optimizeResume(userId, request));
    }
}
