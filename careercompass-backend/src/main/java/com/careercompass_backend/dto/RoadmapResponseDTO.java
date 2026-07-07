package com.careercompass_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO = Data Transfer Object.
 * This is what the controller returns to the client — NOT the Roadmap entity.
 *
 * Why not return the entity directly?
 * The Roadmap entity contains a User object, which contains passwordHash.
 * Returning the entity would leak the password hash in the JSON response.
 * DTOs give us full control over exactly what the client sees.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoadmapResponseDTO {

	private Long roadmapId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int taskCount;
    private Long userId;
    
    /*
     * atsInjected tells the frontend whether ATS bypass tips were prepended.
     * If true, the frontend can show a banner:
     * "Your roadmap includes 2 weeks of ATS bypass preparation"
     */
    private boolean atsInjected;
    
    /*
     * The full AI-generated roadmap description.
     * Gemini produces a summary of what the roadmap covers —
     * we store it here and return it in the response.
     */
    private String aiSummary;
    
}
