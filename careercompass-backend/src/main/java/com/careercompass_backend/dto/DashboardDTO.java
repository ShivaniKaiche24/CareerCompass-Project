package com.careercompass_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

	// Today's Task
	private List<TaskResponseDTO> todaysTasks;
	
	//Progress numbers
	private Integer todayCompleted;   // how many done today
	private Integer todayTotal;       // how many due today
	private Integer currentStreak;    // consecutive days

	// Roadmap overview
    private Integer totalTasks;       // total in roadmap
    private Integer completedTasks;   // completed so far
    private Double completionPercent; // (completed/total) * 100

    /*
     * One DTO gives the frontend EVERYTHING for the dashboard screen.
     * Alternative: 3 separate API calls (today's tasks, streak, progress %).
     * One call = faster load, less network overhead.
     * Trade-off: larger response payload, but dashboard data is small.
     */



}
