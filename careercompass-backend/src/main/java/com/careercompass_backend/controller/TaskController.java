package com.careercompass_backend.controller;

import org.springframework.web.bind.annotation.RestController;

import com.careercompass_backend.dto.TaskResponseDTO;
import com.careercompass_backend.service.TaskService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
	
	private final TaskService taskService;
	
	// To get all User's Task
	@GetMapping("/my-tasks")
	public ResponseEntity<List<TaskResponseDTO>> gatAllTasksFromUser (@RequestParam Long userId) {
		return ResponseEntity.ok(taskService.getALLTasksForUser(userId));
	}
	
	
	
	// To get all Todays Task
	@GetMapping("/today")
	public ResponseEntity<List<TaskResponseDTO>> getAllTodaysTask(@RequestParam  Long userId) {
		return ResponseEntity.ok(taskService.getTodaysTask(userId));
	}
	
	
	// To check if Task is Completed
	@PutMapping("/{taskId}/complete") 
	public ResponseEntity<TaskResponseDTO> updateTaskStatus (@PathVariable Long taskId , @RequestParam Long userId){
		return ResponseEntity.ok(taskService.completeTask(taskId, userId));
	}
	
	
	// To get Tasks by Roadmap 
	@GetMapping("/roadmaps/{roadmapId}")
	public ResponseEntity<List<TaskResponseDTO>> getAllTasksByRoadmap(@PathVariable Long roadmapId , @RequestParam Long userId){
		return ResponseEntity.ok(taskService.getTasksByRoadmap(roadmapId,userId));
	}

	
	@PutMapping("/mark-overdue")
	public ResponseEntity<Map<String, Integer>> updateOverdueTasks(@RequestParam Long userId) {
		int count = taskService.markOverdueTasks(userId);
		return ResponseEntity.ok(Map.of("markedAsMissed" , count));
	}
}
