package com.careercompass_backend.service;

import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careercompass_backend.dto.RoadmapResponseDTO;
import com.careercompass_backend.model.*;
import com.careercompass_backend.repository.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapService {

	 private final UserRepository userRepository;
	    private final RoadmapRepository roadmapRepository;
	    private final TaskRepository taskRepository;
	    private final AtsBypassTipRepository atsBypassTipRepository;

	    /*
	     * GeminiService is injected — RoadmapService orchestrates the process,
	     * GeminiService handles only the AI communication.
	     * This separation of concerns means:
	     * - If we switch from Gemini to another AI, only GeminiService changes
	     * - RoadmapService is testable without making real API calls
	     */
	    private final GeminiService geminiService;

	    // ─────────────────────────────────────────────────────────────────────
	    // MAIN METHOD
	    //
	    // @Transactional: saves Roadmap + all Tasks atomically.
	    // If saving Tasks fails after Roadmap is saved, the entire
	    // operation rolls back — no orphaned roadmap with no tasks.
	    // ─────────────────────────────────────────────────────────────────────
	    @Transactional
	    public RoadmapResponseDTO generateRoadmap(Long userId) {

	        // STEP 1: Load the user — fail fast if not found
	        User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException(
	                "User not found with id: " + userId));

	        // STEP 2: Prevent duplicate active roadmaps
	        // A user should only have ONE active roadmap at a time
	        Optional<Roadmap> existingActive =
	            roadmapRepository.findByUserAndStatus(user, "ACTIVE");
	        if (existingActive.isPresent()) {
	            throw new RuntimeException(
	                "You already have an active roadmap. " +
	                "Complete or pause it before generating a new one.");
	        }

	        log.info("Generating AI roadmap for user {} targeting '{}'",
	            userId, user.getJobTarget());

	        // STEP 3: Call Gemini to generate week-by-week roadmap
	        // This is the key difference from the hardcoded version —
	        // the roadmap is generated fresh for every user's profile
	        Map<Integer, List<String>> aiWeeks = geminiService.generateRoadmapWeeks(
	            user.getDegreeType(),
	            user.getJobTarget(),
	            user.getSkills(),
	            user.getPassoutYear(),
	            user.getIsGapCandidate(),
	            user.getGapMonths()
	        );

	        // STEP 4: Calculate roadmap duration
	        LocalDate startDate = LocalDate.now();
	        LocalDate endDate = startDate.plusWeeks(aiWeeks.size());

	        // STEP 5: Build and save the Roadmap entity
	        // Must save roadmap first — tasks have a FK to roadmap_id
	        Roadmap roadmap = new Roadmap();
	        roadmap.setUser(user);
	        roadmap.setTitle(buildTitle(user.getDegreeType(), user.getJobTarget()));
	        roadmap.setStartDate(startDate);
	        roadmap.setEndDate(endDate);
	        roadmap.setStatus("ACTIVE");

	        Roadmap savedRoadmap = roadmapRepository.save(roadmap);

	        // STEP 6: Build task list
	        List<Task> allTasks = new ArrayList<>();
	        int dayCounter = 1;
	        boolean atsInjected = false;

	        // STEP 7: ATS bypass injection — gap candidates get these FIRST
	        // before the AI-generated tasks begin
	        if (Boolean.TRUE.equals(user.getIsGapCandidate())) {

	            List<AtsBypassTip> tips = atsBypassTipRepository
	                .findByGapSpecificAndIsActiveOrderByRoadmapWeekAsc(true, true);

	            if (!tips.isEmpty()) {
	                log.info("Injecting {} ATS bypass tips for gap candidate", tips.size());

	                for (AtsBypassTip tip : tips) {
	                    Task task = new Task();
	                    task.setRoadmap(savedRoadmap);
	                    task.setTitle("[ATS] " + tip.getTitle());
	                    task.setDescription(tip.getDescription());
	                    task.setDayNumber(dayCounter);
	                    task.setDueDate(startDate.plusDays(dayCounter - 1));
	                    task.setStatus("PENDING");
	                    allTasks.add(task);
	                    dayCounter++;
	                }

	                atsInjected = true;
	            }
	        }

	        // STEP 8: Convert AI-generated weeks to Task objects
	        // Weeks are sorted by key (1, 2, 3...) using TreeMap
	        // to guarantee correct order regardless of how Gemini returned them
	        new TreeMap<>(aiWeeks).forEach((weekNumber, taskTitles) -> {
	            for (String taskTitle : taskTitles) {
	                Task task = new Task();
	                task.setRoadmap(savedRoadmap);
	                task.setTitle(taskTitle);
	                task.setDescription(
	                    "Week " + weekNumber + " — " + user.getJobTarget() + " preparation"
	                );
	                task.setDayNumber(allTasks.size() + 1); // using list size as running counter
	                task.setDueDate(startDate.plusDays(allTasks.size()));
	                task.setStatus("PENDING");
	                allTasks.add(task);
	            }
	        });

	        // STEP 9: Save all tasks in one batch
	        // saveAll() batches the inserts — far more efficient than save() in a loop
	        taskRepository.saveAll(allTasks);

	        log.info("Generated roadmap with {} tasks ({} ATS + {} AI tasks) for user {}",
	            allTasks.size(),
	            atsInjected ? atsBypassTipRepository.findByGapSpecificAndIsActiveOrderByRoadmapWeekAsc(true, true).size() : 0,
	            aiWeeks.values().stream().mapToInt(List::size).sum(),
	            userId);

	        // STEP 10: Build and return DTO — never the entity
	        RoadmapResponseDTO response = new RoadmapResponseDTO();
	        response.setRoadmapId(savedRoadmap.getRoadmapId());
	        response.setTitle(savedRoadmap.getTitle());
	        response.setStartDate(savedRoadmap.getStartDate());
	        response.setEndDate(savedRoadmap.getEndDate());
	        response.setStatus(savedRoadmap.getStatus());
	        response.setTaskCount(allTasks.size());
	        response.setUserId(user.getUserId());
	        response.setAtsInjected(atsInjected);
	        response.setAiSummary(
	            "AI-generated " + aiWeeks.size() + "-week roadmap tailored for " +
	            user.getJobTarget() + " based on your profile."
	        );

	        return response;
	    }

	    // ─────────────────────────────────────────────────────────────────────
	    // GET ACTIVE ROADMAP — no AI call needed, just a DB lookup
	    // ─────────────────────────────────────────────────────────────────────
	    public RoadmapResponseDTO getActiveRoadmap(Long userId) {
	        User user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

	        Roadmap roadmap = roadmapRepository
	            .findByUserAndStatus(user, "ACTIVE")
	            .orElseThrow(() -> new RuntimeException(
	                "No active roadmap found. Generate one first."));

	        List<Task> tasks = taskRepository.findByRoadmap(roadmap);

	        RoadmapResponseDTO response = new RoadmapResponseDTO();
	        response.setRoadmapId(roadmap.getRoadmapId());
	        response.setTitle(roadmap.getTitle());
	        response.setStartDate(roadmap.getStartDate());
	        response.setEndDate(roadmap.getEndDate());
	        response.setStatus(roadmap.getStatus());
	        response.setTaskCount(tasks.size());
	        response.setUserId(userId);

	        return response;
	    }

	    // ─────────────────────────────────────────────────────────────────────
	    // TITLE BUILDER
	    // ─────────────────────────────────────────────────────────────────────
	    private String buildTitle(String degreeType, String jobTarget) {
	        String degree = degreeType != null ? degreeType : "Graduate";
	        String target = jobTarget != null ? jobTarget : "Software Developer";
	        return degree + " → " + target + " Roadmap (AI Generated)";
	    }
}
