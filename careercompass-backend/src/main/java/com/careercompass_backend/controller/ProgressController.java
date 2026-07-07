package com.careercompass_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.ProgressLogDTO;
import com.careercompass_backend.service.ProgressLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/progress")
public class ProgressController {
	
	private final ProgressLogService progressService;
	
	
	/*
     * POST /api/progress/update?userId=1
     * Manually trigger a progress log update.
     * Normally called automatically when a task is completed.
     * Exposed as endpoint for testing purposes.
     */
	@PostMapping("/update")
	public ResponseEntity<ProgressLogDTO> updateDailyLogs( @RequestParam Long userId){
		return ResponseEntity.ok(progressService.updateDailyLog(userId));
	}
	
	
	
	/*
     * GET /api/progress/streak?userId=1
     * Returns current streak count — for the streak badge on dashboard.
     */
	@GetMapping("/streak")
	public ResponseEntity<Map<String,Integer>> getCurrentStreak (@RequestParam Long userId) {
		int streak = progressService.getCurrentStreak(userId);
		return ResponseEntity.ok(Map.of("currentStrak",streak));
	}
	

	 /*
     * GET /api/progress/history?userId=1
     * Returns all ProgressLog entries — for the calendar/chart view.
     * Ordered by logDate DESC (newest first).
     */
	@GetMapping("/history")
	public ResponseEntity<List<ProgressLogDTO>> getAllLogs(@RequestParam Long userId){
		 return ResponseEntity.ok(progressService.getAllLogs(userId));
	}
	
	
	
	
}
