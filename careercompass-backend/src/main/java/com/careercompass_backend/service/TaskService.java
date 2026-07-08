package com.careercompass_backend.service;

import java.time.LocalDate;
import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.careercompass_backend.dto.TaskResponseDTO;
import com.careercompass_backend.model.Roadmap;
import com.careercompass_backend.model.Task;
import com.careercompass_backend.model.User;
import com.careercompass_backend.repository.RoadmapRepository;
import com.careercompass_backend.repository.TaskRepository;
import com.careercompass_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor  // only works when the field is final
@Slf4j
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final RoadmapRepository roadmapRepository;
	private final UserRepository userRepository;
private final ProgressLogService progressLogService;
	
	// Get all Tasks for user
	@Transactional
	public List<TaskResponseDTO> getALLTasksForUser(Long userId) {
		
		User user = userRepository.findById(userId)
				.orElseThrow(()-> new RuntimeException("User not found: " + userId));
				
		Roadmap roadmap = roadmapRepository.findByUserAndStatus(user, "ACTIVE")
				.orElseThrow(() -> new RuntimeException(
			            "No active roadmap found. Generate one first."));
	 List<Task> tasks = taskRepository.findByRoadmap(roadmap);
	 return tasks.stream().map(this::toDTO).collect(Collectors.toList());
	
	}
	
	
	
	// Find Todays Task for User
	public List<TaskResponseDTO> getTodaysTask(Long userId){
		 // Validate user exists first
	    // Without this, wrong userId just returns empty list silently
	    userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException(
	                    "User not found: " + userId));
		
		List <Task> task = taskRepository.findByTodaysTaskForUser(userId , LocalDate.now());
		return task.stream().map(this::toDTO).collect(Collectors.toList());  // map () --> Stram Api method in Java used to transform one object into another object 
	}
	
	// Complete Task 
	@Transactional
	public TaskResponseDTO completeTask(Long taskId , Long userId) {
		
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new RuntimeException("Task not found"));
		
		if(!task.getRoadmap().getUser().getUserId().equals(userId)) {
			throw new RuntimeException("User Not found , You can't make changes");
		}
		
		if(task.getStatus().equals("COMPLETED")) {
			throw new RuntimeException("Your task is already completed");
		}
		
		task.setStatus("COMPLETED");
		Task saved = taskRepository.save(task);
		
		progressLogService.updateDailyLog(userId);
		
		return toDTO(saved);
	}
	
	// Find the tasks by roadmap
	@Transactional
	public List<TaskResponseDTO> getTasksByRoadmap(Long roadmapId ,Long userId) {
		
		// Find the roadmap by ID, throw error if not found
		Roadmap roadmap = roadmapRepository.findById(roadmapId)
				.orElseThrow(() -> new RuntimeException("Roadmap not found"));
		//Check that the roadmap belongs to the requesting user 
		if(!roadmap.getUser().getUserId().equals(userId))
			throw new RuntimeException("Access denied — this roadmap does not belong to you ");
		
		List<Task> task = taskRepository.findByRoadmap(roadmap);
		return task.stream().map(this::toDTO).collect(Collectors.toList());
	}
	
	// Updating overdue tasks as Missed
	@Transactional
	public int markOverdueTasks(Long userId) {
		
		List<Task> task = taskRepository.findOverdueTasksForUser(userId, LocalDate.now());
		
		task.forEach(t-> t.setStatus("MISSED"));
		
		 taskRepository.saveAll(task);
		
		return task.size();
	}
	

	// Converts each Task object into TaskResponseDTO
	
	private TaskResponseDTO toDTO (Task task) {
		
		TaskResponseDTO dto = new TaskResponseDTO();
		
		dto.setTaskId(task.getTaskId());
		dto.setTitle(task.getTitle());
		dto.setDescription(task.getDescription());
		dto.setDayNumber(task.getDayNumber());
		dto.setDueDate(task.getDueDate());
		dto.setStatus(task.getStatus());
		dto.setRoadmapId(task.getRoadmap().getRoadmapId());  // Task Model has field as private Roadmap roadmap which is an object so we used getRoadmap().getRoadmapId() 
		return dto;
	}
	
}
